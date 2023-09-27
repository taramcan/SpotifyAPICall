@Grab ('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1')
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.HttpURLClient
import groovy.xml.slurpersupport.GPathResult
import groovy.xml.XmlParser
import groovy.json.JsonSlurper
import groovy.json.JsonParserType
import groovy.util.*

// assume RSS feed, NOT Atom feed
class News {
    String base = 'https://www.theadvocate.com/search/?q=&t=article&l=35&d=&d1=&d2=&s=start_time&sd=desc&c%5b%5d=baton_rouge/news*,baton_rouge/opinion*,baton_rouge/sports*,new_orleans/sports/saints&nk=%23tncen&f=rss'
    //def slurper = new XmlSlurper(false, false, true)  <= sets some parsing attributes
    def parser = new XmlParser()
    def getNews(int maxCount) {
    def count =  parser.parse(base ).channel[0].item.size()
    count = Math.min(count, maxCount)
    def items =  parser.parse(base ).channel[0].item[0..count-1]
    for (item in items) {
      println item.pubDate.text()
      println item.title.text()
      println item.description.text()
      println "----"
    }
  }    
}

class Weather {
        String base = 'https://www.nhc.noaa.gov/xml/OFFNT4.xml'
       //def slurper = new XmlSlurper(false, false, true)  <= sets some parsing attributes
    def parser = new XmlParser()
    def getWeather(int maxCount) {
        def count =  parser.parse(base ).channel[0].item.size()
        count = Math.min(count, maxCount)
        print(count)
        def items =  parser.parse(base ).channel[0].item[0..count-1]
        println items[0].pubDate[0].text()
        println items[0].title[0].text()
        String description = items[0].description[0].text().toString()
        println description[5178 .. 5425]
        println description[]
        //println items[0].description[0].text()
        println "----"
}
}
/*
 * This class does dangerous screen scraping.  It can break at any time due
 * to an html change on espn.com. Only works for certain leagues. 
 */
class Sports {
      String base = 'https://www.theadvocate.com/search/?q=&t=article&l=35&d=&d1=&d2=&s=start_time&sd=desc&c%5b%5d=baton_rouge/sports/lsu&nk=%23tncen&f=rss'
    // def slurper = new XmlSlurper(false, false, true)  //<= sets some parsing attributes
    def parser = new XmlParser()
    def getSports(int maxCount) {
    def count =  parser.parse(base ).channel[0].item.size()
    count = Math.min(count, maxCount)
    def items =  parser.parse(base ).channel[0].item[0..count-1]
    for (item in items) {
      println item.pubDate.text()
      println item.title.text()
      println item.description.text()
      println "----"
    }
  }  
  
}

// body of script


    def news = new News()
    println "Baton Rouge News"
    def BRStories = news.getNews(1)
    println "-----------------------------"
    def weather = new Weather()
    def nolaWeather = weather.getWeather(1)
    println "-----------------------------"
    def sports = new Sports()
    def LSUTigers = sports.getSports(1)
    println "-----------------------------"