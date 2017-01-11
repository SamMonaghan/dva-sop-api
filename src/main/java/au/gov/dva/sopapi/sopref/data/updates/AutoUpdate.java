package au.gov.dva.sopapi.sopref.data.updates;

import au.gov.dva.sopapi.interfaces.Repository;

public class AutoUpdate {
    public static void Update(SoPLoader soPLoader, SoPChangeDetector soPChangeDetector)
    {
        // check for updates, write to storage


        // load latest
        soPLoader.updateAll(30);

    }
}
