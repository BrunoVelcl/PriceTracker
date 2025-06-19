import os
import requests

def main():
    content = str()
    with open("G:\Dev\Prices\links\kaufland.csv", newline= '\n', encoding="utf-8") as csvFile:
        content = csvFile.read()
    duplicate = list()
    downloaded = list()

    table = readCSV(content)

    i = 0
    for row in table:
        i+= 1
        path = "G:\Dev\Prices\dumpster\\" + row[0]
        if os.path.exists(path):
            duplicate.append(row[0])
            continue
        with open(path, "wb") as store:
            data = requests.get(row[1])
            store.write(data.content)
            downloaded.append(row[0])
            print(f"Downloading {i}/{len(table)}", end="\r")

    duplicateLog = "G:\Dev\Prices\logs\\existing_files.txt"
    with open(duplicateLog, "w", encoding="utf-8") as file:
        for item in duplicate:
            file.write(item + "\n") 
    downloadedLog = "G:\Dev\Prices\logs\\downloaded_files.txt"
    with open(downloadedLog, "w", encoding="utf-8") as file:
        for item in downloaded:
            file.write(item + "\n") 

    print("\x1b[32mDOWNLOAD COMPLETE\x1b[37m")
    print(f"Downloaded files: {len(downloaded)}\nExisting files: {len(duplicate)}")
    print("For details see logs.")
    

# This is needed if we would like to have progress reporting without double iterating with the csv lib
def readCSV(csvFile):  
    table = list()
    row = list()
    iLimit = 0
    for i in range(len(csvFile)):
        match csvFile[i]:
            case '\n':
                row.append(csvFile[iLimit:i])
                table.append(row)
                row = list()
                iLimit = i+1
            case ',':
                row.append(csvFile[iLimit:i])
                iLimit = i+1
    return table


if __name__ == "__main__":
    main()



