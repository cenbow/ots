pScore = 0

pPrice = 100000
minPrice = 100000
mikePrice = 0

for(mkPriceDate in mkPriceDates){
    pField = doc[mkPriceDate]
    if(pField.empty){
        continue;
    }
    mikePrice = pField.value
    if (mikePrice < minPrice) {
        minPrice = mikePrice
    }
}
pScore = pPrice - minPrice

return pScore