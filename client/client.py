#from networktables import NetworkTables
from ftplib import FTP
import os

from math import pi
import json
import sys

import constants as c

if __name__ == '__main__':
    data = c.data
        
    ### FTP ###
    ip = data["ip"]
    customcwd = "/home/lvuser/sysid-tests"
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
        ### Grab file now ###
        print(f"Downloading file: {filename}")
        cwd = os.getcwd()
        with open(filename, 'wb') as fp:
          ftp.retrbinary('RETR '+filename, fp.write)

        #check file was downloaded!
        try:
          with open(os.path.join(cwd, filename), 'r') as f:
            text = f.read()
        except FileNotFoundError:
          print("[{filename}] not found\n Skipping.")
          continue
        
        json_data[test]=json.loads(text)
    
    ftp.quit()#close ftp after files downloaded
    
    json_data["sysid"]="true"
    json_data["test"]="Drivetrain"
    json_data["units"]="Meters"
    json_data["unitsPerRotation"]=str(data["wheelDiameter"]*pi)
    
    with open(data['outputFile'], 'w') as f:
        json.dump(json_data, f, indent=4)
