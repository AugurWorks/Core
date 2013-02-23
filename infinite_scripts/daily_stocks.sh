#!/bin/bash

# Runs daily to add ticker information to the DB

/root/Core/infinite_scripts/ticker_to_db.sh AAPL
/root/Core/infinite_scripts/ticker_to_db.sh GOOG
/root/Core/infinite_scripts/ticker_to_db.sh USO # oil
/root/Core/infinite_scripts/ticker_to_db.sh %5EGSPC # S and P 500
/root/Core/infinite_scripts/ticker_to_db.sh GLD # gold
/root/Core/infinite_scripts/ticker_to_db.sh %5ECOAL # coal
/root/Core/infinite_scripts/ticker_to_db.sh UNG # natural gas
/root/Core/infinite_scripts/ticker_to_db.sh SLV # silver
/root/Core/infinite_scripts/ticker_to_db.sh %5EDFI # defense
/root/Core/infinite_scripts/ticker_to_db.sh DIA # DJIA tracking fund

