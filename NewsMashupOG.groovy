@Grab ('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1')
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.HttpURLClient
//OG
//import groovy.util.XmlParser
//import groovy.util.slurpersupport.GPathResult
//OG
//My improvement
import groovy.xml.slurpersupport.GPathResult
import groovy.xml.XmlParser
//My improvement
import groovy.json.JsonSlurper
import groovy.json.JsonParserType
// assume RSS feed, NOT Atom feed
class News { //https://6abc.com/feed/
//    String base = 'http://www.fox29.com/'
    String base = 'https://6abc.com/feed/'
       //def slurper = new XmlSlurper(false, false, true)  <= sets some parsing attributes
    def parser = new XmlParser()
    def getNews(int maxCount) {
        def count =  parser.parse(base ).channel[0].item.size()
 println("There are exactly ${count} news items.")
        count = Math.min(count, maxCount)
        def items =  parser.parse(base ).channel[0].item[0..count-1]
 //       def items =  parser.parse(base + 'feeds/rssFeed?obfType=RSS_FEED&siteId=200016&categoryId=100000&utm_source=Feed&utm_medium=RSS&utm_campaign=RSS_Syndication').channel[0].item[0..count]
        return items
    }
}

class Weather {
    String base = "http://api.openweathermap.org/data/2.5/"
    def getWeather(String city, String country) {
        def weather = new HttpXmlClient(url: base)
        def resp = weather.request( path: 'weather', query: [q: "${city},${country}",
            appid: '6ddf1159d4a9130f6e79444d0cda0bbb',
            units: 'imperial'
            ])
            
        return resp.data
    }
}
/*
 * This class does dangerous screen scraping.  It can break at any time due
 * to an html change on espn.com. Only works for certain leagues. 
 */
class Sports {
  String base = 'http://espn.com/'
  // Get the sibling of the node.
  GPathResult getSibling(GPathResult node) {
    def kids = node.parent().children().list()
    def idx = kids.indexOf(node)
    def sibling = kids[++idx]
    return sibling
  }
  // Get the first child of the node.
  GPathResult getFirstChild(GPathResult node) {
    def kids = node.children().list()
    def first = kids[0]
    return first
  }
  // Specific to the ESPN schdule page.
  // Get the TABLEs following the H2 tag passed as the parameter.
  // There may be 0, 1, or more TABLE tags found.
  def findDivTables(GPathResult h2) {
    def tables = []
    def kids = h2.parent().children().list()
    def idx = kids.indexOf(h2)
    for (def i = idx + 1; i < kids.size(); i++) {

      def node = kids[i];
      if (node.name() == "DIV") {
        node = getFirstChild(node)
        if (node.name() == "TABLE") {
          tables << node
        }
      } else if (node.name() == "H2") {
        // must stop at next H2
        break
      }
    }
    return tables;
  }

/*
 * return List of Map  - [[Date(String): [awayTeam(String), homeTeam(String), status(String)], ...]
 *               map elements may be an array (e.g. baseball double header)
 *               city is either awayTeam or homeTeam
 * city - city of sports team, e.g. 'Philadelphia'. Cities like "New York" and "Los Angeles"
 *        maybe ambiguous. This can be fixed with a little extra coding.
 * league - one of 'nfl', 'nba', 'mlb'  Not guaranteed to work with other leagues
 */
  List getSchedule(String city, String league) {
    def http = new HTTPBuilder(base)
    def html = http.get(path: "/${league}/schedule", contentType: "text/html")
    // isolate the H2 tags with text that matches a day of the week.
    def dates = html.'**'.findAll {
      it.name() == 'H2' && it.text() =~ /(Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday)/
    }
//println "There are ${dates.size()} game dates."
    def schedule = []
    // Assumptions:
    // 1. H2 tags have dates of games
    // 2. Tags are structured like:
    //    <h2>Date</h2>
    //      <div>  
    //        <table>  // schedule information within table rows
    //      </div>
    // 3. There maybe 0,1,many tables for a date
    dates.each {
      date ->String gameDate = date.text()
//println("${date.name()}: ${gameDate}")
      def tables = findDivTables(date)
      tables.TBODY.each { tbody ->
        // process the table within the H2 date tag.
        // may or may not find the team in the table
//println("${tbody.name()}")
        def gameValue = null
        def rows = tbody.children()
        rows.each { row ->
          // City names are ambiguous for "New York" and "Los Angeles"  
          def awayCity = row.TD[0].A.SPAN.text()
          def homeCity = row.TD[1].DIV.A.SPAN.text()
          def outcome = row.TD[2].A.text()
          // test if the game involves our city (either away or home)
          if (awayCity.equalsIgnoreCase(city) || homeCity.equalsIgnoreCase(city)) {
            gameValue = [awayCity, homeCity, outcome]
            schedule << ["${gameDate}": gameValue]
 //           println "${gameValue[0]} ${gameValue[1]} ${gameValue[2]}"
          }
        }
      }
    }
    def first = true
    return schedule
  }
}

// body of script
    def news = new News()
    def phillyStories = news.getNews(8)
    println "Philly News (unformatted):"
    println phillyStories
    println "-----------------------------"
    def weather = new Weather()
    def phillyWeather = weather.getWeather('Philadelphia', 'US')
    println "Philly Weather (unformatted):"
    println phillyWeather
    println "-----------------------------"
    def sports = new Sports()
//  since it is basketball season
    def city = "Philadelphia"
    def league = "nba" // must be lower case!
    def proSchedule = sports.getSchedule(city, league)
    println "Philly Schedule (unformatted):"		
    println proSchedule

    proSchedule = null