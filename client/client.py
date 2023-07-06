#from networktables import NetworkTables
from ftplib import FTP
import os

from math import pi
import json
import sys

import constants as c

if __name__ == '__main__':
    ### command line arguments ###
    # -d : do not download data but instead look for already-downloaded files
    # -x : delete downloaded files after they have been read
    args = sys.argv[1:]
    args = {
      "-d": ("-d" in args),
      "-x": ("-x" in args),
    }

    data = c.data#constants dictionary
    
    if not args["-d"]:
      ### FTP ###
      ip = data["ip"]
      customcwd = "/home/lvuser/sysid-tests"#folder of the files
      print(f"FTP:\n Ip: {ip}\n Login: anon/anon\n cwd: {customcwd}\nConnecting...")
      
      ftp = FTP(ip)  # connect to host, default port
      ftp.login()                   # user anonymous, passwd anonymous@    
      ftp.cwd(data["cwd"])             # change into "debian" directory

      print("Connected, listing files:")
      ftp.dir()
    
#    ### User Input Just In Case###
#    print("Which file to read?")
#    choices = [(i,f) for i,f in enumerate(ftp.nlst())]#assigns an index to every findable file in the ftp cwd
#    for i in choices:
#      print(f"[{i[0]}]: {i[1]}")# [index]: name
#    
#    n = -1
#    while not -1<n<len(choices):#force user to actually choose an index
#      try:
#        n=int(input("Index: "))
#      except:
#        pass
#    
#    filename=choices[n][1]
    
    ### Reading Files ###
    json_data = {}
    tests = data["tests"]
    
    for test in tests:
        filename=test+".json"
        cwd = os.getcwd()#where this script runs from
        if not args["-d"]:
        ### Grab file ###
          print(f"Downloading file: {filename}")
          with open(filename, 'wb') as fp:
            ftp.retrbinary('RETR '+filename, fp.write)#fancy download function
        else:
          print(f"[-d]: Reading expected file: {filename}")

        #check file exists!
        try:
          with open(os.path.join(cwd, filename), 'r') as f:
            text = f.read()
        except FileNotFoundError:
          print("[{filename}] not found\n Skipping.")
          continue

        #put it in the output json file
        formatted = "{ \"numbers\": ["+text+"]}"
        #print(formatted[:500])
        json_data[test]=json.loads(formatted)["numbers"]

        if args["-x"]:
          os.remove(os.path.join(cwd, filename))
          print(f"Removed {filename}!")
    
    if not args["-d"]:
      ftp.quit()#close ftp after files downloaded
    
    #end settings
    json_data["sysid"]="true"
    json_data["test"]="Drivetrain"
    json_data["units"]="Meters"
    json_data["unitsPerRotation"]=data["wheelDiameter"]*pi
    
    with open(data['outputFile'], 'w') as f:
        json.dump(json_data, f, indent=4)
