    // Replace 'YOUR_API_KEY' with your actual API key
    const clientId = 'dfba4f615d3f4fd08fb04594963a7d8b';
    const clientSecret = '472b8dba0d5b4ee9a552679fb42e59ad';

    document.getElementById('getTrackInfoButton').addEventListener('click', async function () {
        const origin = document.getElementById('artistNameInput').value;
    
        // Replace 'YOUR_API_KEY' with your actual API key
        const apiKey = clientId;
        const secretKey = clientSecret;

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

        const apiToken = await _getToken();
    
        // Step 1: Get Artist ID from User Input
        const getArtistIdUrl = `https://api.spotify.com/v1/search?q=${origin}&type=artist`;
    
        axios.get(getArtistIdUrl, {
            headers: {
                'Authorization' : 'Bearer ' + apiToken
            },
        })
            .then(response => {
                const artistId = response.data.artists.items[0].id;
                
                // Step 2: Pass Artist ID to get top tracks
                const getTrackInfoUrl = `https://api.spotify.com/v1/artists/${artistId}/top-tracks?market=US`;
    
                axios.get(getTrackInfoUrl, {
                    headers: {
                        'Authorization' : 'Bearer ' + apiToken
                    },
                })
                    .then(destinationResponse => {
                            const listItem = document.createElement('li');
                            tracks = []
                            const trackInfo = destinationResponse.data.tracks;
                            for(i = 0; i < trackInfo.length; i++){
                                tracks.push(JSON.stringify(trackInfo[i].name))
                            }
                            //document.getElementById('trackInfo').textContent = tracks;
                            function makeUL(array){
                                var list = document.createElement('ul');
                                for(var i = 0; i < array.length; i++){
                                    var item = document.createElement('li');
                                    item.appendChild(document.createTextNode(array[i]));
                                    list.appendChild(item);
                                }
                                return list;
                            }
                            document.getElementById('trackInfo').appendChild(makeUL(tracks));
                    })

                    .catch(destinationError => {
                        console.error(trackInfoError);
                        document.getElementById('trackInfo').textContent = 'Error fetching artist track info.';
                    });
            })
            .catch(error => {
                console.error(error);
                document.getElementById('trackInfo').textContent = 'Error fetching artist track info.';
            });
    });
    