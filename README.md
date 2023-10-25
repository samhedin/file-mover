# file-mover

My DJ/music producing brother wanted a program to help him sort and organize his music files.
You can hear his music here: https://soundcloud.com/ancapdj

# Installation on windows
First, install [babashka](https://github.com/babashka/babashka#windows "babashka")
Open powershell then run:
```bash
# Note: if you get an error you might need to change the execution policy (i.e. enable Powershell) with
Set-ExecutionPolicy RemoteSigned -scope CurrentUser
Invoke-Expression (New-Object System.Net.WebClient).DownloadString('https://get.scoop.sh')

scoop bucket add scoop-clojure https://github.com/littleli/scoop-clojure
scoop bucket add extras
scoop install babashka
```

Then, dowwnload the script from github to your current powershell directory.
```bash 
curl -o file-mover.clj https://raw.githubusercontent.com/samhedin/file-mover/main/file-mover.clj
```

Now everything should be installed. You can confirm it, and get further instructions, by running
```bash
bb file-mover.clj -h
```
