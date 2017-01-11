package au.gov.dva.sopapi.sopref.data.updates;

public class AutoUpdate {
    public static void Update(SoPLoader soPLoader, SoPChangeDetector soPChangeDetector)
    {
        // check for updates, write to storage

        // load latest
        soPLoader.applyAll(30);

    }
}
