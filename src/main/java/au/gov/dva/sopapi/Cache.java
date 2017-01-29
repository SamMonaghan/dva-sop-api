package au.gov.dva.sopapi;

import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.ServiceDetermination;
import au.gov.dva.sopapi.interfaces.model.SoP;
import com.google.common.collect.ImmutableSet;

class Cache {

    private static Cache instance = null;

    private ImmutableSet<SoP> _allSops;
    private ImmutableSet<ServiceDetermination> _allServiceDeterminations;

    private Cache() {
        _allSops = ImmutableSet.of();
        _allServiceDeterminations = ImmutableSet.of();
    }

    protected static Cache getInstance() {
        if (instance == null)
        {
            synchronized (Cache.class)
            {
                if (instance == null)
                {
                    instance = new Cache();
                }
            }
        }
        return instance;
    }

    public void refresh(Repository repository)
    {

        ImmutableSet<SoP> allSops = repository.getAllSops();
        ImmutableSet<ServiceDetermination> allServiceDeterminations = repository.getServiceDeterminations();

        // atomic
        _allSops = allSops;
        _allServiceDeterminations = allServiceDeterminations;


    }

    public ImmutableSet<SoP> get_allSops() {
        return _allSops;
    }

    public ImmutableSet<ServiceDetermination> get_allServiceDeterminations() {
        return _allServiceDeterminations;
    }


}
