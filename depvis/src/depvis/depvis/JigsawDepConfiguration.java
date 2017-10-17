package depvis;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * DepVis visualization tool for Java 9 Jigsaw modules
 * see https://github.com/accso/java9-jigsaw-depvis
 * 
 * Helper class to read a properties configuration file ("depvis.properties") 
 *   and to maintain the configurations settings (or the defaults)
 */
class JigsawDepConfiguration {
    // only modules with names matching one of these Strings (via "startsWith") are visualized
    private static String[] includeFilter = new String[] { /* "java." ... */ };
    // but also any module with a name matching one of these Strings (via "startsWith") will not be visualized
    private static String[] excludeFilter = new String[] { /*...*/ };	

    static boolean useSystemModules = true;	    // do we want to visualize modules from system?
    static boolean useModulePath    = true;	    // do we want to visualize modules from a module path (if so, depvis.modulePath needs to be set)
    static String  modulePath       = null;     // example "/jigsaw/example/mlib";

    // ---------------------------------------------------------------------------------------------------------------------------------------

    static boolean showRequires = true;           // do we want to visualize requires?
    static boolean showRequiresMandated = true;   // do we want to visualize requires mandated?
    static boolean showRequiresTransitive = true; // do we want to visualize requires transitive?
    static boolean showRequiresStatic = true;     // do we want to visualize requires static?
    static boolean showExports   = true;          // do we want to print exports? (will not be visualized as there is no direction)
    static boolean showExportsTo = true;          // do we want to visualize exports-to?
    static boolean showOpens = true;              // do we want to print opens? (will not be visualized as there is no direction)
    static boolean showOpensTo = true;            // do we want to visualize opens-to?
    static boolean showUses = true;               // do we want to print uses? (will not be visualized as there is no direction)
    static boolean showProvides = true;           // do we want to print provides? (will not be visualized as there is no direction)
    static boolean showContains = true;           // do we want to print contains, i.e. concealed packages? (will not be visualized as there is no direction)
    static boolean showMainClass = true;          // do we want to print the main class? (will not be visualized as there is no direction)

    // ---------------------------------------------------------------------------------------------------------------------------------------

    // Printer options
    static boolean prefixWithModuleName = true;	  // prefix each line with the module name & version (for easier grep's)

    // ---------------------------------------------------------------------------------------------------------------------------------------

    // Visualizer options
    
    static String  outputFileName = "./moduledependencies.dot";					  // path&name of the graphviz output file
    static boolean showLegend = true;											  // show a legend?
    static String  diagramTitle = "Java 9, Jigsaw, Module Dependencies";		  // title of the diagram (in the legend)

    // ---------------------------------------------------------------------------------------------------------------------------------------

    private static ArrayList<String> _includeFilter = new ArrayList<String>();		
    private static ArrayList<String> _excludeFilter = new ArrayList<String>();
    static {
        Arrays.asList(includeFilter).stream().forEach(s -> { _includeFilter.add(s); });
        Arrays.asList(excludeFilter).stream().forEach(s -> { _excludeFilter.add(s); });
    }

    // which modules (based on their names) should be shown? (if empty, all does match)
    static boolean matchesFilters(String name) {

        boolean included = (_includeFilter.isEmpty() ) ? true : false;
        for (String filter: _includeFilter) {
            if (name.startsWith(filter)) { included = true; break; }
        }
        if (!included) return false;

        boolean excluded = false;
        for (String filter: _excludeFilter) {
            if (name.startsWith(filter)) { excluded = true; break; }
        }
        if (excluded) return false;

        return true;
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------

    // read the DepVis properties configuration file
    static void readConfigPropertiesFile(String fileName) throws Exception {
        Properties propsFromFile = new Properties();

        propsFromFile.load(new FileInputStream(new File(fileName)));

        if (propsFromFile.containsKey("depvis.includeFilter")) {
            _includeFilter = new ArrayList<String>();
            String[] tokens = propsFromFile.getProperty("depvis.includeFilter", "").split(",");
            for (String token: tokens) {
                _includeFilter.add(token.trim());
            }
        }
        if (propsFromFile.containsKey("depvis.excludeFilter")) {
            _excludeFilter = new ArrayList<String>();
            String[] tokens = propsFromFile.getProperty("depvis.excludeFilter", "").split(",");
            for (String token: tokens) {
                _excludeFilter.add(token.trim());
            }
        }

        useSystemModules       = Boolean.valueOf(propsFromFile.getProperty("depvis.useSystemModules", Boolean.toString(useSystemModules)));
        useModulePath          = Boolean.valueOf(propsFromFile.getProperty("depvis.useModulePath",    Boolean.toString(useModulePath)));
        modulePath             = propsFromFile.getProperty("depvis.modulePath", modulePath);

        showRequires           = Boolean.valueOf(propsFromFile.getProperty("depvis.showRequires",           Boolean.toString(showRequires)));
        showRequiresMandated   = Boolean.valueOf(propsFromFile.getProperty("depvis.showRequiresMandated",   Boolean.toString(showRequiresMandated)));
        showRequiresTransitive = Boolean.valueOf(propsFromFile.getProperty("depvis.showRequiresTransitive", Boolean.toString(showRequiresTransitive)));
        showRequiresStatic     = Boolean.valueOf(propsFromFile.getProperty("depvis.showRequiresStatic",     Boolean.toString(showRequiresStatic)));
        showExports            = Boolean.valueOf(propsFromFile.getProperty("depvis.showExports",            Boolean.toString(showExports)));
        showExportsTo          = Boolean.valueOf(propsFromFile.getProperty("depvis.showExportsTo",          Boolean.toString(showExportsTo)));
        showOpens              = Boolean.valueOf(propsFromFile.getProperty("depvis.showOpens",              Boolean.toString(showOpens)));
        showOpensTo            = Boolean.valueOf(propsFromFile.getProperty("depvis.showOpensTo",            Boolean.toString(showOpensTo)));
        showUses               = Boolean.valueOf(propsFromFile.getProperty("depvis.showUses",               Boolean.toString(showUses)));
        showProvides           = Boolean.valueOf(propsFromFile.getProperty("depvis.showProvides",           Boolean.toString(showProvides)));

        showContains           = Boolean.valueOf(propsFromFile.getProperty("depvis.showContains",           Boolean.toString(showContains)));
        showMainClass          = Boolean.valueOf(propsFromFile.getProperty("depvis.showMainClass",          Boolean.toString(showMainClass)));

        // Printer options
        prefixWithModuleName   = Boolean.valueOf(propsFromFile.getProperty("depvis.prefixWithModuleName",   Boolean.toString(prefixWithModuleName)));
        
        // Visualizer options
        outputFileName         = propsFromFile.getProperty("depvis.outputFileName", outputFileName);
        showLegend             = Boolean.valueOf(propsFromFile.getProperty("depvis.showLegend", Boolean.toString(showLegend)));
        diagramTitle           = propsFromFile.getProperty("depvis.diagramTitle",  diagramTitle);
    }
}