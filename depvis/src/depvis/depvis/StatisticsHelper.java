package depvis;

/**
 * DepVis visualization tool for Java 9 Jigsaw modules
 * see https://github.com/accso/java9-jigsaw-depvis
 * 
 * Helper class which keeps track of some statistics, i.e. counts modules and relations (filter on/off).
 */
class StatisticsHelper {
    static long start;

    static long now() {
        return System.currentTimeMillis();
    }

    // statistic counters
    static int modTotalCounter = 0;              // number of modules found
    static int modCounter = 0;                   // number of modules found (filtered)
    static int modSystemTotalCounter = 0;        // number of system modules found
    static int modSystemCounter = 0;             // number of system modules found (filtered)
    static int reqTotalCounter = 0;              // number of requires relations found
    static int reqCounter = 0;                   // number of requires relations found
    static int reqMandatedTotalCounter = 0;      // number of requires mandated relations found
    static int reqMandatedCounter = 0;           // number of requires mandated relations found (filtered, no duplicates)
    static int reqTransitiveTotalCounter = 0;    // number of requires transitive relations found
    static int reqTransitiveCounter = 0;         // number of requires transitive relations found (filtered, no duplicates)
    static int reqStaticTotalCounter = 0;        // number of requires static relations found
    static int reqStaticCounter = 0;             // number of requires static relations found (filtered, no duplicates)
    static int exportsToTotalCounter = 0;        // number of exportsTo relations found
    static int exportsToCounter = 0;             // number of exportsTo relations found (filtered, no duplicates)
    static int opensToTotalCounter = 0;          // number of opensTo relations found
    static int opensToCounter = 0;               // number of opensTo relations found (filtered, no duplicates)

    // print out statistics to STDOUT
    static void printStatistics() {
        System.out.println("Created " + JigsawDepConfiguration.outputFileName + " in " + (now()-start) + "ms.");
        System.out.println("  Found " + modTotalCounter                   + " modules in total (shown because of filter/config: "+ modCounter + ")");
        System.out.println("    This includes " + modSystemTotalCounter   + " system modules in total (shown because of filter/config: "+ modSystemCounter + ").");

        System.out.println("  Found " + reqTotalCounter                   + " requires in total (shown because of filter/config: " + reqCounter + ").");
        System.out.println("    This includes " + reqMandatedTotalCounter + " requires mandated in total (shown because of filter/config: " + reqMandatedCounter + ").");

        System.out.println("  Found " + reqTransitiveTotalCounter   + " requires-transitive in total (shown because of filter/config/no-duplicates: " + reqTransitiveCounter + ").");
        System.out.println("  Found " + reqStaticTotalCounter       + " requires-static in total (shown because of filter/config/no-duplicates: " + reqStaticCounter + ").");
        System.out.println("  Found " + exportsToTotalCounter       + " exports-to in total (shown because of filter/config/no-duplicates: " + exportsToCounter + ").");
        System.out.println("  Found " + opensToTotalCounter         + " opens-to in total (shown because of filter/config/no-duplicates: " + opensToCounter + ").");
    }
}
