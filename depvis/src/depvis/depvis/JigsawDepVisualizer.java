package depvis;

import java.io.File;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleDescriptor.Requires.Modifier;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.kohsuke.graphviz.Node;

/**
 * DepVis visualization tool for Java 9 Jigsaw modules
 * see https://github.com/accso/java9-jigsaw-depvis
 * 
 * JigsawDepVisualizer 
 *   This visualizer reads module information from system and/or a configured module path 
 *   including all the relationsships (i.e. requires, exports etc.) 
 *   and then produces a GraphViz output file.
 */
public class JigsawDepVisualizer {
    public static void main(String[] args) throws Exception {
        // load configuration settings from properties file
        if (args.length > 0) {
            JigsawDepConfiguration.readConfigPropertiesFile(args[0]);
        }

        new JigsawDepVisualizer().createGraph();
    }

    JigsawDepVisualizer() {
        // nope
    }

    // create the GraphViz output for all modules (GraphViz nodes) and the relationships (GraphVizedges)
   
    public void createGraph() throws Exception {
        StatisticsHelper.start = StatisticsHelper.now();

        Set<ModuleDescriptor> allModDescs = new HashSet<>();
        findModulesInMP(allModDescs);
        findSystemModules(allModDescs);

        for (ModuleDescriptor modDesc: allModDescs) {
            Node modNode = new Node(modDesc.name());
            
            // create graph node for all modules found
            GraphVizHelper.graph.nodeWith(GraphVizHelper.getNodeStyle(modDesc)).node(modNode);

            // create all types of graph connections between nodes 
            //     note : this might add more nodes (nodes for targets of requires, requires transitive, exports to)
            createGraphForModulesRequires           (allModDescs, modDesc, modNode);
            createGraphForModulesRequiresTransitive (allModDescs, modDesc, modNode);
            createGraphForModulesExportsTo          (allModDescs, modDesc, modNode);
            createGraphForModulesOpensTo            (allModDescs, modDesc, modNode);
        }

        GraphVizHelper.writeGraphToFile();
        StatisticsHelper.printStatistics();
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------

    // find all modules in modulepath and add them to the allMods set
    private void findModulesInMP(Set<ModuleDescriptor> allMods) {
        if (! JigsawDepConfiguration.useModulePath || JigsawDepConfiguration.modulePath == null) return;

        String[] paths = JigsawDepConfiguration.modulePath.split(File.pathSeparator);

        for (String path: paths) {    
            Set<ModuleReference> modRefsFromMLibDirectory = ModuleFinder.of(Paths.get(path)).findAll();
            StatisticsHelper.modTotalCounter += modRefsFromMLibDirectory.size();
    
            long numModulesFound         = _findModulesHelper(allMods, modRefsFromMLibDirectory);
            StatisticsHelper.modCounter += numModulesFound; 
        }
    }

    // find all modules in system and add them to the allMods set
    private void findSystemModules(Set<ModuleDescriptor> allModDescs) {
        if (! JigsawDepConfiguration.useSystemModules) return;

        Set<ModuleReference> modRefsFromSystem = ModuleFinder.ofSystem().findAll();
        StatisticsHelper.modTotalCounter       += modRefsFromSystem.size();
        StatisticsHelper.modSystemTotalCounter += modRefsFromSystem.size();

        long numModulesFound = _findModulesHelper(allModDescs, modRefsFromSystem);
        StatisticsHelper.modCounter       += numModulesFound; 
        StatisticsHelper.modSystemCounter += numModulesFound;
    }

    private long _findModulesHelper(Set<ModuleDescriptor> allModDescs, Set<ModuleReference> modRefs) {
        return modRefs
                  .stream()
                  .filter (modRef -> JigsawDepConfiguration.matchesFilters(modRef.descriptor().name()))
                  .peek   (modRef -> { allModDescs.add(modRef.descriptor());})
                  .count  ();
    }
    
    private Optional<ModuleDescriptor> getModuleDescriptorFromName (Set<ModuleDescriptor> allModDescs, String modName) {
        return allModDescs
                    .stream()
                    .filter(modDesc -> (modDesc.name().equals(modName)))
                    .findFirst();
    }
    
    // ---------------------------------------------------------------------------------------------------------------------------------------

    // create the GraphViz edges for requires
 
    private void createGraphForModulesRequires(Set<ModuleDescriptor> allModDescs, ModuleDescriptor modDesc, Node modNode) {
        // check all module's requires, i.e. "first level" (but no mandated)
        modDesc.requires()
            .stream()
            .peek    (req -> { StatisticsHelper.reqTotalCounter++; })
            .filter  (req -> JigsawDepConfiguration.showRequires)					// show any requires at all?
            .filter  (req -> !req.modifiers().contains(Modifier.MANDATED))			// no requires mandated, as done below
            .filter  (req -> !req.modifiers().contains(Modifier.STATIC))			// no requires static, as done below
            .filter  (req -> JigsawDepConfiguration.matchesFilters(req.name()))	    // if not filtered?
            .forEach (req -> {
                StatisticsHelper.reqCounter++;
                
                ModuleDescriptor targetModDesc = getModuleDescriptorFromName(allModDescs, req.name()).orElse(null);
                GraphVizHelper.graph.fromToNode(
                        modNode,              GraphVizHelper.getNodeStyle(modDesc),
                        new Node(req.name()), GraphVizHelper.getNodeStyle(targetModDesc),
                        null,                 GraphVizHelper.reqStyle); 
            });

        // check all module's requires, i.e. "first level" (this time mandated only)
        modDesc.requires()
            .stream()
            .filter  (req -> req.modifiers().contains(Modifier.MANDATED))
            .filter  (req -> !req.modifiers().contains(Modifier.STATIC))			// no requires static, as done below
            .peek    (req -> { StatisticsHelper.reqMandatedTotalCounter++; })
            .filter  (req -> JigsawDepConfiguration.showRequiresMandated)		    // show any requires (mandated) at all?
            .filter  (req -> JigsawDepConfiguration.matchesFilters(req.name()))   	// if not filtered?
            .forEach (req -> { 
                StatisticsHelper.reqCounter++; 
                StatisticsHelper.reqMandatedCounter++;

                ModuleDescriptor targetModDesc = getModuleDescriptorFromName(allModDescs, req.name()).orElse(null);
                GraphVizHelper.graph.fromToNode(
                        modNode,              GraphVizHelper.getNodeStyle(modDesc),
                        new Node(req.name()), GraphVizHelper.getNodeStyle(targetModDesc),
                        null,                 GraphVizHelper.reqMandatedStyle); 
            });

        // check all module's requires, i.e. "first level" (this time static only)
        modDesc.requires()
            .stream()
            .filter  (req -> req.modifiers().contains(Modifier.STATIC))
            .peek    (req -> { StatisticsHelper.reqStaticTotalCounter++; })
            .filter  (req -> JigsawDepConfiguration.showRequiresStatic)		        // show any requires (static) at all?
            .filter  (req -> JigsawDepConfiguration.matchesFilters(req.name()))  	// if not filtered?
            .forEach (req -> { 
                StatisticsHelper.reqCounter++; 
                StatisticsHelper.reqStaticCounter++;

                ModuleDescriptor targetModDesc = getModuleDescriptorFromName(allModDescs, req.name()).orElse(null);
                GraphVizHelper.graph.fromToNode(
                        modNode,              GraphVizHelper.getNodeStyle(modDesc),
                        new Node(req.name()), GraphVizHelper.getNodeStyle(targetModDesc),
                        null,                 GraphVizHelper.reqStaticStyle); 
            });
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------

    // create the GraphViz edges for requires-transitive (limited to 1-transitive for now)

    private void createGraphForModulesRequiresTransitive(Set<ModuleDescriptor> allModDescs, ModuleDescriptor modDesc, Node modNode) {
        Set<String>   reqTransitives = new HashSet<String>();
        HashMap<String,ArrayList<String>> modNamesWhichHaveReq2AsTransitive = new HashMap<String,ArrayList<String>>();

        // check all module's requires, i.e. "first level"
        modDesc.requires()
            .stream()
            .filter (req -> JigsawDepConfiguration.showRequiresTransitive)		// show any requires-transitive at all?
            .forEach(req1 -> {
                // for each of the requires, search for the module (unfortunately the Requires class only holds the module's name)
                allModDescs
                    .stream()
                    .filter  (req1ModDesc -> (req1ModDesc.name().equals(req1.name())))
                    .forEach (req1ModDesc -> {
                        // now check of this module's requires (2nd level) 
                        req1ModDesc.requires()
                            .stream()
                            // use any if requires-transitive
                            .filter  (req2 -> req2.modifiers().contains(Modifier.TRANSITIVE))
                            .forEach (req2 -> {
                                StatisticsHelper.reqTransitiveTotalCounter++;
            
                                // and save the name of this requires-transitive's target module
                                reqTransitives.add(req2.name());
            
                                // also keep the name of the "middle" module so that we can add its name as a label
                                ArrayList<String> modsWhichHaveReq2AsTransitive = new ArrayList<String>();
                                if (modNamesWhichHaveReq2AsTransitive.get(req2.name()) != null) {
                                    modsWhichHaveReq2AsTransitive = modNamesWhichHaveReq2AsTransitive.get(req2.name());
                                }
                                modsWhichHaveReq2AsTransitive.add(req1.name());
                                modNamesWhichHaveReq2AsTransitive.put(req2.name(), modsWhichHaveReq2AsTransitive);
                            });
                    });
            });

        // for all (non-duplicate) requires-transitive connections
        reqTransitives
            .stream()
            .filter  (req -> JigsawDepConfiguration.showRequiresTransitive)		// show any requires-transitive at all?
            .filter  (reqTransitiveTargetName -> JigsawDepConfiguration.matchesFilters(reqTransitiveTargetName))
            .forEach (reqTransitiveTargetName -> {
                StatisticsHelper.reqTransitiveCounter++;

                ModuleDescriptor targetModDesc = getModuleDescriptorFromName(allModDescs, reqTransitiveTargetName).orElse(null);
                GraphVizHelper.graph.fromToNode(
                         modNode,                       GraphVizHelper.getNodeStyle(modDesc),
                         new Node(reqTransitiveTargetName), GraphVizHelper.getNodeStyle(targetModDesc),
                         modNamesWhichHaveReq2AsTransitive.get(reqTransitiveTargetName).toString(), GraphVizHelper.reqTransitiveStyle);
            });
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------

    // create the GraphViz edges for exports-to

    private void createGraphForModulesExportsTo(Set<ModuleDescriptor> allModDescs, ModuleDescriptor modDesc, Node modNode) {
        Set<String> exportsTargets = new HashSet<String>();

        // check all module's exports, i.e. "first level"
        modDesc.exports()
            .stream()
            .filter  (export -> JigsawDepConfiguration.showExportsTo)		// show any exports-to at all?
            .filter  (export -> export.isQualified())
            .forEach (export -> {
                export.targets()
                    .stream()
                    .forEach(expTargetName -> {
                        StatisticsHelper.exportsToTotalCounter++;
        
                        // add any of the exports targets to a set (needed to avoid duplicates)
                        exportsTargets.add(expTargetName);
                    });
            });

        exportsTargets
            .stream()
            .filter  (expTargetName -> JigsawDepConfiguration.showExportsTo)		// show any exports-to at all?
            .filter  (expTargetName -> JigsawDepConfiguration.matchesFilters(expTargetName))
            .forEach (expTargetName -> {
                StatisticsHelper.exportsToCounter++;

                ModuleDescriptor targetModDesc = getModuleDescriptorFromName(allModDescs, expTargetName).orElse(null);
                GraphVizHelper.graph.fromToNode(
                        modNode,                 GraphVizHelper.getNodeStyle(modDesc),
                        new Node(expTargetName), GraphVizHelper.getNodeStyle(targetModDesc),
                        null,                    GraphVizHelper.exportsToStyle);
            });
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------

    // create the GraphViz edges for opens-to

    private void createGraphForModulesOpensTo(Set<ModuleDescriptor> allModDescs, ModuleDescriptor modDesc, Node modNode) {
        Set<String> opensTargets = new HashSet<String>();

        // check all module's opens, i.e. "first level"
        modDesc.opens()
            .stream()
            .filter  (open -> JigsawDepConfiguration.showOpensTo)		// show any opens-to at all?
            .filter  (open -> open.isQualified())
            .forEach (open -> {
                open.targets()
                    .stream()
                    .forEach(opTargetName -> {
                        StatisticsHelper.opensToTotalCounter++;
        
                        // add any of the opens targets to a set (needed to avoid duplicates)
                        opensTargets.add(opTargetName);
                    });
            });

        opensTargets
            .stream()
            .filter  (opTargetName -> JigsawDepConfiguration.showOpensTo)		// show any opens-to at all?
            .filter  (opTargetName -> JigsawDepConfiguration.matchesFilters(opTargetName))
            .forEach (opTargetName -> {
                StatisticsHelper.opensToCounter++;

                ModuleDescriptor targetModDesc = getModuleDescriptorFromName(allModDescs, opTargetName).orElse(null);
                GraphVizHelper.graph.fromToNode(
                        modNode,                 GraphVizHelper.getNodeStyle(modDesc),
                        new Node(opTargetName),  GraphVizHelper.getNodeStyle(targetModDesc),
                        null,                    GraphVizHelper.opensToStyle);
            });
    }
}