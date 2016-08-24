package depvis;

import java.io.File;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleDescriptor.Requires.Modifier;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.kohsuke.graphviz.Node;

/**
  * JigsawDepVisualizer 
  *   reads module information from system and/or module path 
  *   including all the relationsships and produces a GraphViz output file
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
            //     note : this might add more nodes (nodes for targets of requires, requires public, exports to)
            createGraphForModulesRequires       (allModDescs, modDesc, modNode);
            createGraphForModulesRequiresPublic (allModDescs, modDesc, modNode);
            createGraphForModulesExportsTo      (allModDescs, modDesc, modNode);
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
            .filter  (req -> !req.modifiers().contains(Modifier.MANDATED))
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
            .peek    (req -> { StatisticsHelper.reqTotalMandatedCounter++; })
            .filter  (req -> JigsawDepConfiguration.showRequiresMandated)			// show any requires (mandated) at all?
            .filter  (req -> JigsawDepConfiguration.matchesFilters(req.name()))	// if not filtered?
            .forEach (req -> { 
                StatisticsHelper.reqCounter++; 
                StatisticsHelper.reqMandatedCounter++;

                ModuleDescriptor targetModDesc = getModuleDescriptorFromName(allModDescs, req.name()).orElse(null);
                GraphVizHelper.graph.fromToNode(
                        modNode,              GraphVizHelper.getNodeStyle(modDesc),
                        new Node(req.name()), GraphVizHelper.getNodeStyle(targetModDesc),
                        null,                 GraphVizHelper.reqMandatedStyle); 
            });
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------

    // create the GraphViz edges for requires-public (limited to 1-transitive for now)

    private void createGraphForModulesRequiresPublic(Set<ModuleDescriptor> allModDescs, ModuleDescriptor modDesc, Node modNode) {
        Set<String>   reqPublics = new HashSet<String>();
        HashMap<String,ArrayList<String>> modNamesWhichHaveReq2AsPublic = new HashMap<String,ArrayList<String>>();

        // check all module's requires, i.e. "first level"
        modDesc.requires()
            .stream()
            .filter (req -> JigsawDepConfiguration.showRequiresPublic)		// show any requires-public at all?
            .forEach(req1 -> {
                // for each of the requires, search for the module (unfortunately the Requires class only holds the module's name)
                allModDescs
                    .stream()
                    .filter  (req1ModDesc -> (req1ModDesc.name().equals(req1.name())))
                    .forEach (req1ModDesc -> {
                        // now check of this module's requires (2nd level) 
                        req1ModDesc.requires()
                            .stream()
                            // use any if requires-public
                            .filter  (req2 -> req2.modifiers().contains(Modifier.PUBLIC))
                            .forEach (req2 -> {
                                StatisticsHelper.reqPublicTotalCounter++;
            
                                // and save the name of this requires-public's target module
                                reqPublics.add(req2.name());
            
                                // also keep the name of the "middle" module so that we can add its name as a label
                                ArrayList<String> modsWhichHaveReq2AsPublic = new ArrayList<String>();
                                if (modNamesWhichHaveReq2AsPublic.get(req2.name()) != null) {
                                    modsWhichHaveReq2AsPublic = modNamesWhichHaveReq2AsPublic.get(req2.name());
                                }
                                modsWhichHaveReq2AsPublic.add(req1.name());
                                modNamesWhichHaveReq2AsPublic.put(req2.name(), modsWhichHaveReq2AsPublic);
                            });
                    });
            });

        // for all (non-duplicate) requires-public connections
        reqPublics
            .stream()
            .filter  (req -> JigsawDepConfiguration.showRequiresPublic)		// show any requires-public at all?
            .filter  (reqPublicTargetName -> JigsawDepConfiguration.matchesFilters(reqPublicTargetName))
            .forEach (reqPublicTargetName -> {
                StatisticsHelper.reqPublicCounter++;

                ModuleDescriptor targetModDesc = getModuleDescriptorFromName(allModDescs, reqPublicTargetName).orElse(null);
                GraphVizHelper.graph.fromToNode(
                         modNode,                       GraphVizHelper.getNodeStyle(modDesc),
                         new Node(reqPublicTargetName), GraphVizHelper.getNodeStyle(targetModDesc),
                         modNamesWhichHaveReq2AsPublic.get(reqPublicTargetName).toString(), GraphVizHelper.reqPublicStyle);
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
}