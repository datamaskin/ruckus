var request = require('request');
var cheerio = require('cheerio');
var url = 'http://www.rotowire.com/football/injuries.htm';
request.get(url, function (error, response, body) {
    if (!error && response.statusCode == 200) {
        var $ = cheerio.load(body);
        $('.nflinj-teams').find('tr').each(function(i, elem) {
            var line = '';
            $(this).find('td').each(function(j, elem2) {
                line += $(this).text().replace(/\s/g, " ") + ',';
            });
            line = line.substring(0, line.length - 1);
            if(line.length > 0) {
                console.log(line.trim());
            }
        });
    }
});