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
    # -m=[id] : only collect data from a specific module
    #  [id] can be -1 or 1 or l or r or left or right
    iargs = sys.argv[1:]
    args = {
      "-d": ("-d" in iargs),
      "-x": ("-x" in iargs),
      "-m": ("-x" in iargs),
      "-m=": None
    }
    
    if args["-m"]:
      i=0
      while "-m" not in iargs[i]:
        i+=1
      #dump the arg value into memory
      val = iargs[i][3:]
      if val in ["1","r","right"]:
        print("[-m]: Only collecting RIGHT side swerve data!")
        args["-m="]=1
      elif val in 
        print("[-m]: Only collecting LEFT side swerve data!")
        args["-m="]=-1
      else:
        print("[-m]: Invalid argument passed to -m!")
        exit()

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
        
        raw = json.loads(formatted)["numbers"]
        #Logger Json Value format:
          #0 Timer.getFPGATimestamp
          #1 getLeftVoltage
          #2 getRightVoltage
          #3 getLeftPosition
          #4 getRightPosition
          #5 getLeftVelocity
          #6 getRightVelocity
          #7 getRotation2d().getDegrees() / 360.0
          #8 getAngularVelocity() / Math.PI / 2.0
        swaps=[]#which slots to override with the opposite side's data?
        if args["-m="] = 1:#right
          swaps=[
            (0,1),#make the left motor pretend to have the right's VOLTAGE
            (3,4),#make the left motor pretend to have the right's POSITION
            (5,6),#make the left motor pretend to have the right's VELOCITY
          ]
        elif args["-m="] = -1:#left
          swaps=[
            (1,0),#make the RIGHT motor pretend to have the LEFT's voltage
            (4,3),#make the RIGHT motor pretend to have the LEFT's pos
            (6,5),#make the RIGHT motor pretend to have the LEFT's vel
          ]
        
        #apply swaps
        if swaps!=[]:
          for stamp in raw["numbers"]:
            for pair in swaps:
              stamp[pair[0]] = stamp[pair[1]]
        #print(raw[:500])
        
        json_data[test]=raw

        
        if args["-x"]:
          os.remove(os.path.join(cwd, filename))
          print(f"[-x]: Removed {filename}!")
    
    if not args["-d"]:
      ftp.quit()#close ftp after files downloaded
    
    #end settings
    json_data["sysid"]="true"
    json_data["test"]="Drivetrain"
    json_data["units"]="Meters"
    json_data["unitsPerRotation"]=data["wheelDiameter"]*pi
    
    with open(data['outputFile'], 'w') as f:
        json.dump(json_data, f, indent=4)
