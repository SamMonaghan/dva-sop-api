package au.gov.dva.sopapi.sopref.data.updates;

public class AutoUpdate {
    public static void UpdateStorage(SoPLoader soPLoader, SoPChangeDetector soPChangeDetector)
    {
        // check for updates, write to storage
        // detect changes,
        // create changes
        // save to DB if do not exist already


        // load latest
        soPLoader.applyAll(30);

    }
}
