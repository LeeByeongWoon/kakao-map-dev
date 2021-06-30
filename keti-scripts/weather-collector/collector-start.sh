export KETI_WEATHER_COLLECTOR_HOME="/app/keti-weather-collector-1h"


if [ "$#" -lt 1 ]; then
    echo "$# is Illegal number of parameters."
    echo "Usage Example: $0 --active=dev --target-ids=1,2 --scrap-interval=1"
    exit 1
fi


for opt in "$@"
do
    key=$(echo $opt | awk '{split($1,arr,"="); print arr[1]}')
    value=$(echo $opt | awk '{split($1,arr,"="); print arr[2]}')

    if [ "${key}" = "--active" ]; then
        if [ "${value}" != "dev" -a "${value}" != "prod" ]; then
            echo "--active: dev or prod."
            exit 1
        else
            active=${value}
	fi
    elif [ "${key}" = "--target-ids" ]; then
        if [ "${value}" = 0 ]; then
            echo "--target-ids: 0 is Illegal number of parameters."
            exit 1
        else
            ids=${value}
	fi
    elif [ "${key}" = "--scrap-interval" ]; then
        if [ "${value}" -lt 1 -o "${value}" -gt 24 ]; then
            echo "--scrap-interval: supported 1 to 24"
            exit 1
        else
	    interval=${value}
	    if [ "${interval}" -eq 1 ]; then
                cron="0 0 * * * *"
	    elif [ "${interval}" -eq 24 ]; then
	        cron="0 0 0 * * *"
	    else
	        cron="0 0 0/${value} * * *"
	    fi
	fi
    fi
done


sudo -u root java -Xms1g -Xmx1g -jar ${KETI_WEATHER_COLLECTOR_HOME}/libs/keti-weather-collector-0.0.1-SNAPSHOT.jar    \
 	--spring.profiles.active="${active}"              \
 	--spring.weatherApi.target-id="${ids}"                       \
	--spring.weatherApi.scrap-interval=${interval}                \
 	--spring.weatherApi.scheduled-cron="${cron}" &
