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
import groovy.net.http.*


def base = 'https://www.theadvocate.com/search/?q=&t=article&l=35&d=&d1=&d2=&s=start_time&sd=desc&c%5b%5d=baton_rouge/news*,baton_rouge/opinion*,baton_rouge/sports*,new_orleans/sports/saints&nk=%23tncen&f=rss'
// Hard-wired to the Philadelphia News
def parser = new XmlParser()
def count = parser.parse(base).channel[0].item.size()
def items = parser.parse(base).channel[0].item[0..count-1]
for (item in items) {
 println item.pubDate.text()
 println item.title.text()
 print item.description.text()
 println "----"
}

// def source = "http://api.openweathermap.org/data/2.5/"
// def weather = new HttpURLClient(url: source)
// // JSON request:
// def resp = weather.request( path: 'weather', query: [q:
// 'Philadelphia,US', appid: '6ddf1159d4a9130f6e79444d0cda0bbb', units:
// 'imperial' ])
// println "JSON response: ${resp.data}\n"
// println "Description: ${resp.data.weather.description}"
// println "It is currently ${resp.data.weather.main[0]} in Philadelphia."
// println "The temperature is ${resp.data.main.temp} degrees Fahrenheit"
