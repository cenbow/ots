import  org.elasticsearch.common.logging.*;
ESLogger logger = ESLoggerFactory.getLogger('myscript');

drprice = 99999999

if(currentday)
{
	drrules = _source["drrules"]
	for(drrule in drrules)
	{
		if(searchtime >= drrule["starttime"] && searchtime < drrule["endtime"])
		{
			drprice = drrule["drprice"]
		}
	}
}
for(mkPriceDate in mkPriceDates)
{
 	tmpValue = doc[mkPriceDate].value
 	if(tmpValue < drprice)
 	{
 		drprice = tmpValue
 	}
}

if(drprice >= drpricemin && drprice <= drpricemax)
{
	return true
}
return false
