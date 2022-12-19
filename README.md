# polovniautomobiliCrawler
A simple crawler to check new ads

polovniautomobili.rs is the most popular Serbian website for selling used cars. 

I made this project to be able to find a good car for myself. 
As website does not provide historical data I decided to collect it and keep in local DB.  

//test request example
### get search test
GET http://localhost:1400/cardb/parseNewAds?link=testPages/audi_search_page.html

//SQL to get ad and matching price from DB
SELECT a.ad_id, NAME, CITY, b.PRICE, b.LAST_UPDATED FROM SEARCH_RESULTS as a
JOIN PRICE_HISTORICAL_DATA as b
on a.ID= b.ADUUID
