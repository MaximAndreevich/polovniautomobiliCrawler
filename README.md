# polovniautomobiliCrawler
A simple crawler to check new ads

polovniautomobili.rs is the most popular Serbian website for selling used cars. 

I made this project to be able to find a good car for myself. 
As website does not provide historical data I decided to collect it and keep in local DB.  

//test request example
### get search test
GET http://localhost:1400/cardb/parseNewAds?link=testPages/audi_search_page.html

### SQL to get ad and matching price from DB

SELECT  UNIQUE_NUMBER, TITLE, URL, PRICE, CREATE_DATE, CHANGE_DATE
FROM CAR_LISTING
JOIN PRICE_HISTORY
ON CAR_LISTING.ID = PRICE_HISTORY.CAR_LISTING_ID 
