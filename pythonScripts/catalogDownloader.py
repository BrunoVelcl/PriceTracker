from urllib.request import urlretrieve
from urllib.error import HTTPError, URLError
from datetime import datetime
import sys
import json


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
            
            try:
                urlretrieve(url, downloadDir + filename)

            except HTTPError as e:
                currentTime = datetime.now()
                log.write(logLineFormat(currentTime, "HTTP Error", id, url, e.msg))
                exitCode = 1

            except URLError as e:
                currentTime = datetime.now()
                log.write(logLineFormat(currentTime, "URL Error", id,  url, e.msg))
                exitCode = 1

            except Exception as e:
                currentTime = datetime.now()
                log.write(logLineFormat(currentTime, "Unexpected Error", id,  url, e.msg))
                exitCode = 1

        if exitCode == 0:
            currentTime = datetime.now()
            log.write(logLineFormat(currentTime, "OK", 0, "All files fetched successfully"))        

    sys.exit(exitCode)

def logLineFormat(datetime: str, status:str, id:int, url:str, error: Optional[BaseException] = None) -> str:
    return f"{datetime} | {status} | {id} | {url} | {error} \n"

if __name__ == "__main__":
    catalogDownloader(sys.argv[1], sys.argv[2])