## TESTING ## 
## Get track uri
# $track = Read-Host "Track Name: "
# $modifiedtrack = $track -replace(" ", "%20")
# $artist = Read-Host "Artist Name: "
# $modifiedartist = $artist -replace(" ", "%20")
# # note: remember to change access token 
# $access_token = "BQD5nJv8B6Pb9fUYAMbLDu3thzH-dTar0uRRajeWSny3gfnwaYF-yGidKb7d7pjluNxB1H8XY27uOuej1WtcdFkK19TQD7aGOCXg780YcyB5CYjjKPXIZCwBPjF6GnzhhUn4Rp91y18EBxGgwOs2obCCl_CufcCZLyIepwmP1-We4P287NKIawhUsdfyQ_jVo4fPVDsvn_RKPc_J4ptknsatbSx7mJxC"
# $test = curl.exe -X GET "https://api.spotify.com/v1/search?q=track:$($modifiedtrack)%20artist:$($modifiedartist)&type=track&limit=1" -H "Authorization: Bearer $access_token" 
# $output = $test | Select-String -Pattern 'spotify:track:[^"]*' -AllMatches | % { $_.Matches.Value }
# $output1 = $test | Select-String -Pattern "The access token expired" -AllMatches | % { $_.Matches.Value }
# if ($output1) {
#     $output1 
# }
# else {
#     $output
# }

## TESTING ##   
# #to add music to spotify using powershell
# curl.exe -i -X POST "https://api.spotify.com/v1/playlists/6Ysidspe89YNFS0fshmcAe/tracks?uris=$($output)" -H "Authorization: Bearer BQAAaI5LDvpM0JvUyq1zilRV2KQaJefjm4IHZJTQVNf6ddrlld5bSRUPCkQvzmDDXX_pqvPSSTRvTbgYawV0IPuV13qHwTVdBPH0wNCooSaYlBt7PgYLFJpQQF1Lh-ilnfW6qoqYLCrSeICuqTmy0F8Ojh4skMp3SYDZDI8G4It8_Ix2cx4kJdxhIqI33Q_lghUbjrJdQhOT" -H "Accept: application/json" -d 0

## FOR ACTUAL USE ##
## IMPORTANT NOTE: music filename format -> [Artist Name] - [Song Title]
## side note (optional): import songs from your phone's local music storage to computer. e.g. C:\Music
## Get Songs Names
## Using the delimiter "-", the filename will be split into two, [Artist Name] & [Song Title]
## change path accordingly 
$directory = "D:\Music"
Get-ChildItem -path $directory -Force -Recurse | Select-Object BaseName | ConvertTo-Csv -NoTypeInformation | Select-Object -Skip 1 | ForEach-Object {$_ -replace '"',''} | Set-Content -Path "D:\Music1\Songs.csv"
$File = "D:\Music1\Songs.csv"
$header = Import-Csv $File -Delimiter '-'
$header | Export-Csv $File -NoTypeInformation
