package depvis;

import java.io.File;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleDescriptor.Exports;
import java.lang.module.ModuleDescriptor.Opens;
import java.lang.module.ModuleDescriptor.Requires;
import java.lang.module.ModuleDescriptor.Provides;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * DepVis visualization tool for Java 9 Jigsaw modules
 * see https://github.com/accso/java9-jigsaw-depvis
 * 
 * JigsawDepPrinter (second main class besides JigsawDepVisualizer)
 *   This printer produces module information and prints it all to STDOUT.
 *   Its configuration is done in a properties configuration file (same as for JigsawDepVisualizer)
 *   
 *   see also Java9 class jdk.jartool/sun.tools.jar.Main for its option "--describe-module"
 */ 
public class JigsawDepPrinter {
    public static void main(String[] args) throws Exception {
        // load configuration settings from properties file
        if (args.length > 0) {
            JigsawDepConfiguration.readConfigPropertiesFile(args[0]);
        }

        new JigsawDepPrinter().printModuleDescriptions();
    }

    JigsawDepPrinter() {
        // nope
    }

    public void printModuleDescriptions() throws Exception {
        Set<ModuleDescriptor> allModDescs = new HashSet<>();

// search all modules in module path
        if (   JigsawDepConfiguration.useModulePath
            && JigsawDepConfiguration.modulePath != null)
        {
            String[] paths = JigsawDepConfiguration.modulePath.split(File.pathSeparator);
            
            for (String path: paths) {
                ModuleFinder.of(Paths.get(path)).findAll()
                    .stream()
                    .filter ((ModuleReference modRef) -> JigsawDepConfiguration.matchesFilters(modRef.descriptor().name()))
                    .forEach((ModuleReference modRef) -> { allModDescs.add(modRef.descriptor()); });
            }
        }

// search all system modules
        if (JigsawDepConfiguration.useSystemModules) {
            ModuleFinder.ofSystem().findAll()
                .stream()
                .filter ((ModuleReference modRef) -> JigsawDepConfiguration.matchesFilters(modRef.descriptor().name()))
                .forEach((ModuleReference modRef) -> { allModDescs.add(modRef.descriptor()); });
        }
        
// for each module
        allModDescs.stream()
          .sorted()
          .forEach((ModuleDescriptor mod) -> {
            
// print the module name and version and is-automatic?
             System.out.println("\nModule " + mod.toNameAndVersion() + " (automatic: " + mod.isAutomatic() + ")");

// print the module's requires (and all it's qualifiers)
             mod.requires()
                .stream()
                .filter((Requires req) -> JigsawDepConfiguration.showRequires)	     // show any requires at all?
                .sorted()
                .forEach((Requires req) -> {
                    System.out.println((JigsawDepConfiguration.prefixWithModuleName ? mod.toNameAndVersion() : " ") 
                    		+ " requires " + req.name()
                            + ((req.modifiers().isEmpty()) ? "" : " "+req.modifiers().toString()));
                });

// print the module's exports
             mod.exports()
                .stream()
                .sorted(Comparator.comparing(Exports::source))
                .filter ((Exports exp) -> JigsawDepConfiguration.showExports)     // show any exports at all?
                .filter ((Exports exp) -> ! exp.isQualified())
                .forEach((Exports exp) -> {
                    System.out.println((JigsawDepConfiguration.prefixWithModuleName ? mod.toNameAndVersion() : " ")
                    		+ " exports " + exp.source());
                });

// print the module's exports-to
             mod.exports()
                .stream()
                .sorted(Comparator.comparing(Exports::source))
                .filter ((Exports exp) -> JigsawDepConfiguration.showExportsTo)		// show any exports-to at all?
                .filter ((Exports exp) -> exp.isQualified())
                .forEach((Exports exp) -> {
                    System.out.println((JigsawDepConfiguration.prefixWithModuleName ? mod.toNameAndVersion() : " ") 
                    		+ " exports " + exp.source() + " to " + exp.targets().toString());
                });

// print the module's open
             mod.opens()
                .stream()
                .sorted(Comparator.comparing(Opens::source))
                .filter ((Opens op) -> JigsawDepConfiguration.showOpens)		// show any opens at all?
                .filter ((Opens op) -> ! op.isQualified())
                .forEach((Opens op) -> {
                    System.out.println((JigsawDepConfiguration.prefixWithModuleName ? mod.toNameAndVersion() : " ") 
                    		+ " opens " + op.source());
                });

// print the module's exports-to
             mod.opens()
                .stream()
                .sorted(Comparator.comparing(Opens::source))
                .filter ((Opens op) -> JigsawDepConfiguration.showOpensTo)		// show any opens-to at all?
                .filter ((Opens op) -> op.isQualified())
                .forEach((Opens op) -> {
                    System.out.println((JigsawDepConfiguration.prefixWithModuleName ? mod.toNameAndVersion() : " ") 
                    		+ " opens " + op.source() + " to " + op.targets().toString());
                });
             
// print the module's uses
             mod.uses()
                .stream()
                .sorted()
                .filter ((String us) -> JigsawDepConfiguration.showUses)		// show any uses at all?
                .forEach((String us) -> {
                    System.out.println((JigsawDepConfiguration.prefixWithModuleName ? mod.toNameAndVersion() : " ") 
                    		+ " uses " + us);
                });

// print the module's provides
        	mod.provides()
	           .stream()
	           .sorted()
	           .filter ((Provides pr) -> JigsawDepConfiguration.showProvides)		// show any provides at all?
	           .forEach((Provides pr) -> {
	               System.out.println((JigsawDepConfiguration.prefixWithModuleName ? mod.toNameAndVersion() : " ") 
	               		+ " provides " + pr.service() + " with " + pr.providers());
	           });

// print all other packages, i.e. non-exported/non-open packages
        	if (JigsawDepConfiguration.showContains) {
	             Set<String> concealed = new TreeSet<>(mod.packages());
	             mod.exports().stream().map(Exports::source).forEach(concealed::remove);
	             mod.opens().stream().map(Opens::source).forEach(concealed::remove);
	             concealed.forEach(pkg -> 
	             	System.out.println((JigsawDepConfiguration.prefixWithModuleName ? mod.toNameAndVersion() : " ") 
	             				+ " contains " + pkg));
        	}

// print the main class (if any)
        	if (JigsawDepConfiguration.showMainClass) {
	             mod.mainClass().ifPresent(clazz -> 
	             	System.out.println((JigsawDepConfiguration.prefixWithModuleName ? mod.toNameAndVersion() : " ") 
	             			    + " main-class " + clazz));
        	}
          });
    }
}
