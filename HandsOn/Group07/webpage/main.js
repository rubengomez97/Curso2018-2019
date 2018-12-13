$("#populate").html("");

var data = {};
var listOfNeighbourhoods = []

var ourDataQuery = [
    'PREFIX sosa:<http://www.w3.org/ns/sosa/>',
    'PREFIX property:<http://www.group07.linkeddata.org/property#>',
    'PREFIX geo:<http://www.w3.org/2003/01/geo/wgs84_pos#>',
    "select  distinct ?StationName ?Address ?Neighbourhood ?Substance ?Time (?Value as ?Average) where {",
        "?obsPr property:hasSubstance ?Substance.",
        "?observation sosa:observedProperty  ?obsPr.",
        "?observation sosa:resultTime ?Time.",
        "?Station rdfs:label ?StationName.",
        "?Station property:address ?Address.",
        "?Station geo:location ?Location.",
        "?Location property:neighbourhood ?Neighbourhood.",
    "{",
        "select distinct ?Substance ?Station ?Value where {",
        "?observation a <http://purl.oclc.org/NET/ssnx/ssn#Observation>.",
        "?observation sosa:observedProperty ?obsProp.",
        "?obsProp property:hasSubstance ?Substance. ",
        // "?obsProp property:hasSubstance <http://dbpedia.org/resource/Sulfur_dioxide>. ",
        "?obsProp sosa:madeBySensor ?Station.",
        // "?obsProp sosa:madeBySensor <http://www.group07.linkeddata.org/individual/station/28079004>.",
        "?obsProp property:hasMetricValue ?Value. ",
        "}group by  ?Station",
        "}",
    "} group by ?StationName  ?Address ?Neighbourhood ?Substance"
].join(' ');

var treatData = (_data) => {
    console.log(_data)
    _data.results.bindings.forEach((measurement) => {
        var neighbourhoodCode = measurement.Neighbourhood.value.split('/')[4];

        if (measurement.Time != '2017-02-01T00:00:00+01:00') {
            console.log('algo', measurement);
        }

        if (data[neighbourhoodCode] == undefined) {
            var neighbourhood = {
                neighbourhood: neighbourhoodCode,
                airInfo: {}
            }
            data[neighbourhoodCode] = neighbourhood;
            // listOfNeighbourhoods.push(neighbourhoodCode);
        }

        var neighbourhood = data[neighbourhoodCode];
        var substanceCode = measurement.Substance.value.split('/')[4];
        if (neighbourhood.airInfo[substanceCode] == undefined) {
            var substance = [
                [[], [], [], [], [], [], [], [], [], [], [], []],
                [[], [], [], [], [], [], [], [], [], [], [], []]
            ]
            neighbourhood.airInfo[substanceCode] = substance;
        }

        var substance = neighbourhood.airInfo[substanceCode];

        var time = measurement.Time.value.split('-');
        var year = parseInt(time[0]);
        if (year == 2018) {
            year = 1;
        }
        else {
            year = 0;
        }
        var month = parseInt(time[1]) - 1;

        // console.log(substanceCode, month, year, measurement.Average.value)

        substance[year][month].push(parseFloat(measurement.Average.value))
    });
}

$.ajax({
    crossDomain: true,
    dataType: 'jsonp',
    url: "http://localhost:8890/sparql?query=" + encodeURIComponent(ourDataQuery) + "&format=json",
    success: (_data) => {
        //console.log('Success receives', JSON.parse(JSON.stringify(_data)));
        // console.log(_data)
        treatData(_data);
        console.log(data)

        Object.keys(data).forEach((neighbourhoodCode) => {
            var neighbourhood = data[neighbourhoodCode];

            Object.keys(neighbourhood.airInfo).forEach((substanceCode) => {
                Object.keys(neighbourhood.airInfo[substanceCode]).forEach((year) => {
                    if (neighbourhood.airInfo[substanceCode][year] != undefined) {
                        Object.keys(neighbourhood.airInfo[substanceCode][year]).forEach((month) => {
                            var valuesForMonth = neighbourhood.airInfo[substanceCode][year][month];
                            // console.log(substanceCode, year, neighbourhoodCode, valuesForMonth)

                            if (neighbourhood.airInfo[substanceCode][year][month] != undefined && neighbourhood.airInfo[substanceCode][year][month].length != 0) {
                                var average = 0;

                                valuesForMonth.forEach((value) => {
                                    average += value;
                                });

                                average /= valuesForMonth.length;

                                neighbourhood.airInfo[substanceCode][year][month] = average;
                            }
                            else {
                                neighbourhood.airInfo[substanceCode][year][month] = 0;
                            }

                        })
                    }
                    else {
                    }


                })
            })
        })












        var neighbourhoodDataQuery = [
            "SELECT distinct ?neighbourhoodcode ?name ?population ?area ?imageURI",
            "WHERE {",
            "?neighbourhoodcode wdt:P31 wd:Q3032114.",
            "?neighbourhoodcode wdt:P1082 ?population.",
            "?neighbourhoodcode wdt:P2046 ?area.",
            "?neighbourhoodcode rdfs:label ?name.",
            "FILTER (lang(?name) in ('es')).",
            "OPTIONAL { ?neighbourhoodcode wdt:P18 ?imageURI. }",
            "}"
        ].join(" ");
        $.ajax({
            url: "https://query.wikidata.org/sparql?query=" + encodeURIComponent(neighbourhoodDataQuery) + "&format=json",
            success: function (_data) {
                _data.results.bindings.forEach((wdNeigh) => {
                    Object.keys(data).forEach((neighbourhoodCode) => {
                        var neighbourhood = data[neighbourhoodCode];
                        if ("http://www.wikidata.org/entity/" + neighbourhood.neighbourhood == wdNeigh.neighbourhoodcode.value) {
                            neighbourhood.neighbourhoodInfo = {};
                            neighbourhood.neighbourhoodInfo.population = parseInt(wdNeigh.population.value);
                            neighbourhood.neighbourhoodInfo.area = parseFloat(wdNeigh.area.value);
                            neighbourhood.neighbourhoodInfo.name = wdNeigh.name.value;
                            if (wdNeigh.imageURI != undefined) {
                                neighbourhood.neighbourhoodInfo.imageURI = wdNeigh.imageURI.value;
                            }
                            $("#populate").after('<li><div class="collapsible-header"><i class="material-icons">location_city</i>'
                                + neighbourhood.neighbourhoodInfo.name
                                + '</div><div class="collapsible-body"><span>'
                                + '<img class="circle" src="' + neighbourhood.neighbourhoodInfo.imageURI + '" width="200" height="200"><br><br>'
                                + 'Population: ' + neighbourhood.neighbourhoodInfo.population + ' citizens.<br>'
                                + 'Area:  ' + neighbourhood.neighbourhoodInfo.area + ' km².<br>'
                                + '</span>'
                                + '<div id="' + neighbourhoodCode + '"></div>'
                                + '</div>');
                        }
                    });
                });





                console.log(data);







                for (var e in data) {

                    var s = '<div class="row">';

                    for (var prop in data[e]["airInfo"]) {
                        s = s + ' <div class="col s3"><canvas id="' + e + prop + '" width="10" height="10"></canvas></div>';
                    }

                    s = s + '</div></div></li>';
                    $("#" + data[e].neighbourhood).after(s);
                }

                for (var e in data) {

                    for (var prop in data[e]["airInfo"]) {

                        var labels = ["Jan-2017", "Feb-2017", "Mar-2017", "Apr-2017", "May-2017", "Jun-2017", "Jul-2017", "Aug-2017", "Sep-2017", "Dec-2017", "Jan-2018", "Feb-2018", "Mar-2018", "Apr-2018", "May-2018"];
                        var measures = data[e]["airInfo"][prop][0].concat(data[e]["airInfo"][prop][1]);
                        console.log(measures)
                        var new_measures = [];
                        for (var i = 0; i < measures.length; i++) {
                            var set = measures[i];
                            console.log(set)
                            // var sum = set.reduce((a,b) => a + b, 0);
                            var sum = set;
                            // set.forEach(elem => sum += elem);
                            // sum = sum/ set.length;
                            new_measures.push(sum);
                        }
                        var clean_measures = [];
                        var clean_labels = [];
                        for (var i = 0; i < new_measures.length; i++) {
                            // console.log(new_measures[i])
                            if (isNaN(new_measures[i]) || new_measures[i] == undefined || new_measures[i] == 0 || i >= labels.length) {

                            } else {
                                clean_measures.push(new_measures[i]);
                                clean_labels.push(labels[i]);
                            }
                        }

                        var ctx = document.getElementById(e + prop).getContext('2d');
                        var name = prop.replace('_', ' ');
                        var myChart = new Chart(ctx, {
                            type: 'line',
                            data: {
                                labels: clean_labels,
                                datasets: [{
                                    label: name,
                                    data: clean_measures,
                                    backgroundColor: [
                                        'rgba(50, 100, 255, 0.2)'
                                    ],
                                    borderColor: [
                                        'rgba(100, 100, 255, 1)'

                                    ],
                                    borderWidth: 2
                                }]
                            },
                            options: {
                                scales: {
                                    yAxes: [{
                                        ticks: {
                                            beginAtZero: true
                                        }
                                    }]
                                }
                            }
                        });
                    }
                }















            },
            async: false
        });







    },
    async: false
});












$(document).ready(function () {
    $('.collapsible').collapsible();
    console.log(data);
});
