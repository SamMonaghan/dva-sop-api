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
        case _ => LsSoPFactory // todo: generic factory for unknown sops
      }
    }
}
