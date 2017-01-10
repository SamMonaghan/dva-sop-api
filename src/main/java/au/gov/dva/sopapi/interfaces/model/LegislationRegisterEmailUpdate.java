package au.gov.dva.sopapi.interfaces.model;

import java.net.URL;

public interface LegislationRegisterEmailUpdate {
    String getInstrumentTitle(); // eg "Amendment Statement of Principles concerning panic disorder (No. 101 of 2016)"
    String getInstrumentDescription(); // eg "This instrument amends the Statement of Principles concerning panic disorder, as determined by the Repatriation Medical Authority."
    String getUpdateDescription(); // eg "Item was amended" or "Some or all of this item commenced on 2/11/2016"
    URL getRegisterLink(); // eg https://www.legislation.gov.au/Details/F2014L01390
}
