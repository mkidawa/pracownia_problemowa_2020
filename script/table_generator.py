import glob
import os
import pathlib

from pandas import DataFrame
from pandas import read_csv

"""
"""

# VAR ------------------------------------------------------------------------ #
CSV = "*.csv"
LOCATION_FILE_NAMES = glob.glob(os.path.join(pathlib.Path(os.getcwd()), CSV))
COLUMNS = [
    "detection0",
    "timeToAllDetection",
    "numberOfOrdinaryVehicle",
    "numberOfAttackers",
    "attackerToOrdinaryRatio",
    "attackerNumber0",
    "attackerNumber1",
    "attackerNumber2",
    "attackerNumber3",
    "attackerNumber4",
    "attackerNumber5",
    "attackerNumber6",
    "attackerNumber7",
    "attackerNumber8",
    "attackerNumber9",
    "attackerNumber10",
    "attackerNumber11"
]


# DEF ------------------------------------------------------------------------ #
def read_files() -> None:
    results: str = ""
    for it in LOCATION_FILE_NAMES:
        primitive_data: DataFrame = read_csv(it, usecols=COLUMNS)
        primitive_data.to_csv("temp.csv", index=False, header=False)
        with open("temp.csv", "r") as file:
            res = file.read()
        print(res)
        os.remove("temp.csv")

        res = res.replace(",", " & ")
        results += res

    with open("final_table.txt", "w") as file:
        file.write(results)


# MAIN ----------------------------------------------------------------------- #
def main() -> None:
    read_files()
    display_finish()


# UTIL ----------------------------------------------------------------------- #
def display_finish() -> None:
    print("------------------------------------------------------------------------")
    print("FINISHED")
    print("------------------------------------------------------------------------")


if __name__ == "__main__":
    main()
