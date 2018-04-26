# INSTRUCTIONS:
# Use the image file to find coordinates for the stations. Most image
# editors allow you to see the coordinate of the mouse. This (not very stylish)
# script creates a value for x and y between 0 and 1 that can be used
# independently of the canvas size in the simulation, as long as the coordinates
# of all stations have been obtained from the same image. 

finished = False
station_strings = []
while not finished:
  station_name = input("Enter station name: ")
  if station_name == "quit":
    finished = True
    break
  x_coordinate = float(input("Enter x coordinate: "))
  y_coordinate = float(input("Enter y coorinate: "))
  relative_x_coordinate = x_coordinate / 1028
  relative_y_coordinate = y_coordinate / 868
  station_strings.append(station_name + "/" + str(relative_x_coordinate) + "/" + str(relative_y_coordinate))
for string in station_strings:
  print(string)
