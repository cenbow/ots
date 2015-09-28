distance = doc["pin"].distanceInKm(lat, lon)
score = 0
dScore = 0;
if(distance <= dt1){
    dScore += 5
}
else if(distance <= dt2){
    dScore += 3
}
else{
    dScore += 2
}
dScore = dScore * 0.2

pScore = 0
for(mkPriceDate in mkPriceDates){ 
    pField = doc[mkPriceDate]
    if(pField.empty){
        continue;
    }   
    if(pField.value <= pt1){
        pScore += 5 
    }
    else if(pField.value <= pt2){
        pScore += 3
    }
    else{ 
        pScore += 2
    }
}   
pScore = pScore * 0.1
    
uScore = doc["priority"].value * 0.4

return dScore + pScore + uScore