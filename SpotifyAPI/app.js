    // Personalied IDs from account for API call
    const clientId = 'dfba4f615d3f4fd08fb04594963a7d8b';
    const clientSecret = '472b8dba0d5b4ee9a552679fb42e59ad';

    document.getElementById('getTrackInfoButton').addEventListener('click', async function () {
        //collect user input
        const origin = document.getElementById('artistNameInput').value;
    
        const apiKey = clientId;
        const secretKey = clientSecret;

        //check user placed text in the field
        const isEmpty = str => !str.trim().length;
        if(isEmpty(document.getElementById('artistNameInput').value) ) {
            document.getElementById('seeAlso').textContent = "";
            document.getElementById('trackInfo').textContent = "Please enter something in the search box";
            return false;
        }
        else{

        //Get personalized Spotify token for this API call
        const _getToken = async () => {
            const result = await fetch('https://accounts.spotify.com/api/token', {
                method: 'POST',
                headers: {
                    'Content-Type' : 'application/x-www-form-urlencoded', 
                    'Authorization' : 'Basic ' + btoa( clientId + ':' + clientSecret)
                },
                body: 'grant_type=client_credentials'
            });    
            const data = await result.json();
            return data.access_token;
        }
        //Store token in a variable
        const apiToken = await _getToken();    
        // Step 1: Get Artist URL from spotify based on User Input
        const getArtistIdUrl = `https://api.spotify.com/v1/search?q=${origin}&type=artist`;
    
        axios.get(getArtistIdUrl, {
            headers: {
                'Authorization' : 'Bearer ' + apiToken
            },
        })
            .then(response => {
                //Grab the ID of the first (therefore likely most popular) artist with this name
                const artistId = response.data.artists.items[0].id;
                
                // Step 2: Pass Artist ID to get top tracks
                const getTrackInfoUrl = `https://api.spotify.com/v1/artists/${artistId}/top-tracks?market=US`;
    
                axios.get(getTrackInfoUrl, {
                    headers: {
                        'Authorization' : 'Bearer ' + apiToken
                    },
                })
                    .then(destinationResponse  => {
                            const listItem = document.createElement('li');
                            tracks = []
                            document.getElementById('trackInfo').textContent = ' ';
                            const trackInfo = destinationResponse.data.tracks;
                            //iterate through all tracks to get strings of song titles
                            for(i = 0; i < trackInfo.length; i++){
                                tracks.push(JSON.stringify(trackInfo[i].name))
                            }
                            //place strings of song titles into ordered list
                            function makeUL(array){
                                var list = document.createElement('ol');
                                for(var i = 0; i < array.length; i++){
                                    var item = document.createElement('li');
                                    item.appendChild(document.createTextNode(array[i]));
                                    list.appendChild(item);
                                }
                                return list;
                            }
                            //display ordered list on index.html
                            document.getElementById('trackInfo').appendChild(makeUL(tracks));
                            
                    })

                    //Step 3:  Pass the user input's artist ID to get related artists
                    const getRelatedArtists = `https://api.spotify.com/v1/artists/${artistId}/related-artists`;
                    axios.get(getRelatedArtists,{
                        headers: {
                            'Authorization' : 'Bearer ' + apiToken
                        },
                    })
                    //recommend related artists
                    .then(destinationResponse =>{
                        document.getElementById('seeAlso').textContent = ' ';
                        const relatedArtists = destinationResponse.data.artists[0].name;
                        document.getElementById('seeAlso').innerHTML="You may also like: " + `${relatedArtists}`;
                    })
                    
                    //validate user input on user side
                    .catch(destinationError => {
                        document.getElementById('trackInfo').textContent = 'Error fetching artist track info.';
                    });
            })
            //return error when server-side validation fails
            .catch(error => {
                console.error(error);
                document.getElementById('seeAlso').textContent = "";
                document.getElementById('trackInfo').textContent = 'Error. \"' + `${origin}` + '\" is not a known artist.';
            });
        }

    });
    