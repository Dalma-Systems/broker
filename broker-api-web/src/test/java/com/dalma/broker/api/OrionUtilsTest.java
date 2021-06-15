package com.dalma.broker.api;

import com.dalma.broker.contract.dto.station.work.BrokerWorkStationInputDto;
import com.dalma.broker.contract.dto.warehouse.BrokerWarehouseInputDto;
import com.dalma.contract.dto.robot.BrokerRobotInputDto;
import com.dalma.contract.dto.warehouse.material.BrokerWarehouseMaterialInputDto;

public abstract class OrionUtilsTest extends BaseITest {

    protected BrokerRobotInputDto createRobotRequest() {
        BrokerRobotInputDto input = new BrokerRobotInputDto();
        input.setBattery(85);
        input.setLatitude(41.165013593);
        input.setLongitude(-8.606473804);
        input.setName("robot from tests");
        input.setStatus("idle");
        input.setVersion("1.2 Beta");
        return input;
    }

    protected BrokerRobotInputDto createUpdateRobotRequest() {
        BrokerRobotInputDto input = new BrokerRobotInputDto();
        input.setBattery(79);
        input.setStatus("moving");
        input.setLatitude(41.16);
        input.setLongitude(-8.60);
        return input;
    }

    protected BrokerRobotInputDto mergeRequests(BrokerRobotInputDto robotCreated, BrokerRobotInputDto robotUpdated) {
        if (robotUpdated.getBattery() != null) {
            robotCreated.setBattery(robotUpdated.getBattery());
        }
        if (robotUpdated.getLatitude() != null) {
            robotCreated.setLatitude(robotUpdated.getLatitude());
        }
        if (robotUpdated.getLongitude() != null) {
            robotCreated.setLongitude(robotUpdated.getLongitude());
        }
        if (robotUpdated.getName() != null) {
            robotCreated.setName(robotUpdated.getName());
        }
        if (robotUpdated.getStatus() != null) {
            robotCreated.setStatus(robotUpdated.getStatus());
        }
        if (robotUpdated.getVersion() != null) {
            robotCreated.setVersion(robotUpdated.getVersion());
        }
        return robotCreated;
    }

    protected BrokerWarehouseInputDto createWarehouseRequest() {
        BrokerWarehouseInputDto input = new BrokerWarehouseInputDto();
        input.setLatitude(40.70884);
        input.setLongitude(-8.4906427);
        input.setAngle(1.670796327);
        input.setName("warehouse from tests");
        input.setStatus("ready");
        input.setErpId("0075");
        return input;
    }

    protected BrokerWarehouseMaterialInputDto createWarehouseMaterialRequest() {
        BrokerWarehouseMaterialInputDto input = new BrokerWarehouseMaterialInputDto();
        input.setBatch("00012");
        input.setErpId("12345");
        input.setMaterial("hood");
        input.setQuantity(15.0);
        input.setUnit("KG");
        return input;
    }

    protected BrokerWorkStationInputDto createStationRequest(String erpId) {
        BrokerWorkStationInputDto input = new BrokerWorkStationInputDto();
        input.setLatitude(41.165013593);
        input.setLongitude(-8.606473804);
        input.setName("work station from tests");
        input.setStatus("work");
        input.setAngle(1.870796310);
        input.setErpId(erpId);
        return input;
    }
}
