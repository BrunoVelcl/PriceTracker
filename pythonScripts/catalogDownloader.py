from datetime import datetime
import sys
import json
import requests

def catalogDownloader(downloadDir:str, urlAndFilenameJson:str):

    urlAndFilename = None
    data = json.loads(urlAndFilenameJson)
    if isinstance(data, dict):
        urlAndFilename = [data]
    else:
        urlAndFilename = data

    print("Opening log")
    with open(downloadDir + "catalogDownloaderLog.txt", "a") as log:
        
        exitCode = 0

        for kv in urlAndFilename:
            id = kv["id"]
            url = kv["url"]
            filename = kv["filename"]  

            r = requests.get(url, verify=False)
            
            if r.status_code != 200:
                currentTime = datetime.now()
                log.write(logLineFormat(currentTime, r.status_code, id, url, r.reason))
                exitCode = 1
                continue

            with open(downloadDir + filename, "wb") as recievedFile:
                recievedFile.write(r.content)

        if exitCode == 0:
            currentTime = datetime.now()
            log.write(logLineFormat(currentTime, "OK", 0, "All files fetched successfully"))        

    sys.exit(exitCode)

def logLineFormat(datetime: str, status:str, id:int, url:str, error: Optional[str] = "Good") -> str:
    return f"{datetime} | {status} | {id} | {url} | {error} \n"

if __name__ == "__main__":
    catalogDownloader(sys.argv[1], sys.argv[2])