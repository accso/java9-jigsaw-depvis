package depvis;

class StatisticsHelper {
    static long start;

    static long now() {
        return System.currentTimeMillis();
    }

    static int modTotalCounter = 0;			// number of own modules found
    static int modCounter = 0;				// number of modules found (filtered)
    static int modSystemTotalCounter = 0;	// number of modules from system found
    static int modSystemCounter = 0;		// number of modules from system found (filtered)
    static int reqTotalCounter = 0;			// number of requires connections found
    static int reqCounter = 0;				// number of requires connections found
    static int reqTotalMandatedCounter = 0;	// number of requires mandated connections found
    static int reqMandatedCounter = 0;		// number of requires mandated connections found
    static int reqPublicTotalCounter = 0;	// number of requires public   connections found
    static int reqPublicCounter = 0;		// number of requires public   connections found (no duplicates)
    static int exportsToTotalCounter = 0;	// number of exportsTo connections found
    static int exportsToCounter = 0;		// number of exportsTo connections found (no duplicates)

    static void printStatistics() {
        System.out.println("Created " + JigsawDepConfiguration.outputFileName + " in " + (now()-start) + "ms.");
        System.out.println("  Found " + modTotalCounter                   + " modules in total (shown because of filter/config: "+ modCounter + ")");
        System.out.println("    This includes " + modSystemTotalCounter   + " system modules in total (shown because of filter/config: "+ modSystemCounter + ").");

        System.out.println("  Found " + reqTotalCounter                   + " requires in total (shown because of filter/config: " + reqCounter + ").");
        System.out.println("    This includes " + reqTotalMandatedCounter + " requires mandated in total (shown because of filter/config: " + reqMandatedCounter + ").");

        System.out.println("  Found " + reqPublicTotalCounter   + " requires-public in total (shown because of filter/config/no-duplicates: " + reqPublicCounter + ").");
        System.out.println("  Found " + exportsToTotalCounter   + " exports-to in total (shown because of filter/config/no-duplicates: " + exportsToCounter + ").");
    }
}
