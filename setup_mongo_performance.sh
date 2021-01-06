
#!/bin/bash

lat=12.9
lng=77.8
while read -r latitude longitude
do
    lat=$latitude
    lng=$longitude
done < coordinates.txt

echo "Your coordinates latitude/longitude are: " $lat $lng

cd ~/workspace
# Either clone or pull latest.
QEATS_SHARED_RESOURCES="${HOME}/workspace/qeats_shared_resources"
if [ ! -d $QEATS_SHARED_RESOURCES ]
then
    git clone git@gitlab.crio.do:me_qeats_shared/qeats_shared_resources.git $QEATS_SHARED_RESOURCES
else
    cd $QEATS_SHARED_RESOURCES
    git pull
fi

if systemctl status mongodb.service | grep active > /dev/null; then
    echo "MongoDB is running..."
else
    echo "MongoDB not running; Exiting"
    exit -1
fi

# Ensure a clean slate & populate all collections
mongo restaurant-database --eval "db.dropDatabase()" 
mongorestore --host localhost --db restaurant-database --gzip --archive=$QEATS_SHARED_RESOURCES/restaurants-perf-gzipped-mongo-dump

pip3 install pymongo

# Localize restaurants
echo "Localizing restaurants for your region, so that you can see them when you load the app..."
python3 $QEATS_SHARED_RESOURCES/localize_restaurants.py $lat $lng 250
