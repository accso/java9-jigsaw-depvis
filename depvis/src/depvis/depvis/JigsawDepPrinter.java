package depvis;

import java.io.File;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleDescriptor.Exports;
import java.lang.module.ModuleDescriptor.Requires;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

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
        .forEach(mod -> {
            
// print the module name and version and is-automatic?
            System.out.println("Module " + mod.toNameAndVersion() + " (automatic: " + mod.isAutomatic() + ")");

// print the module's requires
            mod.requires()
                .stream()
                .filter((Requires req) -> JigsawDepConfiguration.showRequires)	     // show any requires at all?
                .sorted()
                .forEach((Requires req) -> {
                    System.out.println("  requires " + req.name() + 
                            ((req.modifiers().isEmpty()) ? "" : " "+req.modifiers().toString()));
                });

// print the module's exports-to
            mod.exports()
                .stream()
                .filter ((Exports exp) -> JigsawDepConfiguration.showExportsTo)		// show any exports-to at all?
                .filter ((Exports exp) -> exp.isQualified())
                .sorted(Comparator.comparing(Exports::source)) // unfortunately, Exports does not implement Comparable :-(
                .forEach((Exports exp) -> {
                    System.out.println("  exports " + exp.source() + " to " + exp.targets().toString());
                });
        });
    }
}