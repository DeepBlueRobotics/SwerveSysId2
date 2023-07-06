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
    iargs = sys.argv[1:]
    args = {
      "-d": ("-d" in iargs),
      "-x": ("-x" in iargs),
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
    cwd = os.getcwd()#where this script runs from
    
    for i in range(0,4):#for each of the 4 modules
      json_data = {}
      tests = data["tests"]

      for test in tests:
        separateData=[]
        testData=[]

        #download all the data
        filename=test+"-"+i+".json"
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

        #put each module's data in storage
        formatted = "{ \"numbers\": ["+text+"]}"#pretend it's real json so we can load it
        raw = json.loads(formatted)["numbers"]
        
        #Logger Json Value format:
          #0 Timer.getFPGATimestamp
          #1 voltage
          #2 position
          #3 velocity
          #print(raw[:500])

        json_data[test]=testData

        
        if args["-x"]:
          os.remove(os.path.join(cwd, filename))
          print(f"[-x]: Removed {filename}!")
        
        #end settings
        json_data["sysid"]="true"
        json_data["test"]="Simple"
        json_data["units"]="Rotations"
        json_data["unitsPerRotation"]=1
        #write file
        moduleStr = ["FR","FL","BL","BR"]
        outName = "data-"+moduleStr+".json"
        with open(outName, 'w') as f:
            json.dump(json_data, f, indent=4)
            print(f"Wrote to {outName}!")
    
    
    
    #close ftp after all files downloaded
    if not args["-d"]:
      ftp.quit()
