package au.gov.dva.sopapi.sopref.parsing.factories

import au.gov.dva.sopapi.sopref.parsing.implementations.{CartilageTearSoPFactory, LsSoPFactory, OsteoarthritisSoPFactory}
import au.gov.dva.sopapi.sopref.parsing.traits.SoPFactory

object SoPFactoryLocator {

  def findFactory(registerId : String) : SoPFactory =
    {
      registerId match  {
        case "F2014L00933" => LsSoPFactory
        case "F2014L00930" => LsSoPFactory
        case "F2011C00491" => OsteoarthritisSoPFactory
        case "F2011C00492" => OsteoarthritisSoPFactory
        case "F2010L01666" => CartilageTearSoPFactory
        case "F2010L01667" => CartilageTearSoPFactory
        case "F2010L01668" => CartilageTearSoPFactory
        case "F2010L01669" => CartilageTearSoPFactory
        case "F2010L02850" => CartilageTearSoPFactory
        case "F2010L02851" => CartilageTearSoPFactory
        case "F2012L01789" => CartilageTearSoPFactory
        case "F2012L01790" => CartilageTearSoPFactory
        case _ => LsSoPFactory // todo: generic factory for unknown sops
      }
    }
}
