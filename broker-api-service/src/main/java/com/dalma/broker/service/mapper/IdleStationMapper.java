package com.dalma.broker.service.mapper;

import com.dalma.broker.contract.dto.station.idle.BrokerIdleStationInputDto;
import com.dalma.broker.contract.dto.station.idle.BrokerIdleStationSummaryOutputDto;
import com.dalma.broker.fiware.orion.connector.entity.station.idle.IdleStation;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class IdleStationMapper extends OrionBaseMapper {
    private IdleStationMapper() {
    }

    @Component
    public static class IdleStationInputDtoToIdleStation extends PropertyMap<BrokerIdleStationInputDto, IdleStation> {

        @Override
        protected void configure() {
            using(idToOrionId).map(source).setId(null);
            using(coordinatesToOrionLocation).map(source).setLocation(null);
            using(stringToOrionString).map(source.getName()).setName(null);
            using(stringToOrionString).map(source.getStatus()).setStatus(null);
        }
    }

    @Component
    public static class IdleStationToIdleStationSummaryOutputDto
            extends PropertyMap<IdleStation, BrokerIdleStationSummaryOutputDto> {

        @Override
        protected void configure() {
            using(strAttrToAttrOutput).map(source.getName()).setName(null);
            using(strAttrToAttrOutput).map(source.getStatus()).setStatus(null);
            using(geoCoordinatesToAttrOutput).map(source.getLocation()).setCoordinates(null);
            using(doubleAttrToAttrOutput).map(source.getLocation().getMetadata().getAngle()).setAngle(null);
            using(dateToDateOutput).map(source.getDateCreated()).setDateCreated(null);
            using(dateToDateOutput).map(source.getDateModified()).setDateModified(null);
            using(strBaseAttrToAttrOutput).map(source.getRefRobot()).setRobotId(null);
        }
    }
}
