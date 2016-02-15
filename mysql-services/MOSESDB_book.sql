#SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
#SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
#SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `MOSESDB2` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;

CREATE TABLE error_msg (
error_msg VARCHAR(256) NOT NULL PRIMARY KEY)
ENGINE = InnoDB;

INSERT INTO error_msg VALUES ('Process Foreign Key Constraint Violated for SLA!!!');
INSERT INTO error_msg VALUES ('SLA Foreign Key Constraint Violated for Agreement!!!');
INSERT INTO error_msg VALUES ('User Foreign Key Constraint Violated for Agreement!!!');
INSERT INTO error_msg VALUES ('SLA Foreign Key Constraint Violated for Group!!!');
INSERT INTO error_msg VALUES ('AbstractService Foreign Key Constraint Violated for ConcreteService!!!');
INSERT INTO error_msg VALUES ('AbstractService Foreign Key Constraint Violated for AbstractOperation!!!');
INSERT INTO error_msg VALUES ('ConcreteService Foreign Key Constraint Violated for ConcreteOperation!!!');
INSERT INTO error_msg VALUES ('AbstractOperation Foreign Key Constraint Violated for ConcreteOperation!!!');  
INSERT INTO error_msg VALUES ('Process Foreign Key Constraint Violated for Process_has_AbstractService!!!');
INSERT INTO error_msg VALUES ('AbstractService Foreign Key Constraint Violated for Process_has_AbstractService!!!');  
INSERT INTO error_msg VALUES ('Group Foreign Key Constraint Violated for ConcreteOperation_has_Group!!!');
INSERT INTO error_msg VALUES ('ConcreteOperation Foreign Key Constraint Violated for ConcreteOperation_has_Group!!!');


-- -----------------------------------------------------
-- Table `MOSESDB2`.`User`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`User` (
  `Username` VARCHAR(150) NOT NULL ,
  `Password` VARCHAR(150) NOT NULL ,
  `Name` VARCHAR(150) NOT NULL ,
  `Surname` VARCHAR(150) NOT NULL ,
  PRIMARY KEY (`Username`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `MOSESDB2`.`Process`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`Process` (
  `ProcessName` VARCHAR(150) NOT NULL ,
  `ProcessNS` VARCHAR(150) NOT NULL ,
  `ProcessOperation` VARCHAR(150) NOT NULL ,
  `ProcessEndpoint` VARCHAR(150) NOT NULL ,
  `ProcessGraph` VARCHAR(2048) NOT NULL ,
  `Stateful` TINYINT(1) NULL ,
  PRIMARY KEY (`ProcessName`) )
ENGINE = MyISAM;


-- -----------------------------------------------------
-- Table `MOSESDB2`.`SLA`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`SLA` (
  `ProcessClass` VARCHAR(150) NOT NULL ,
  `Process_ProcessName` VARCHAR(150) NOT NULL ,
  `SLAFile` BLOB NULL ,
  `SLAConstraints` BLOB NULL ,
  `SLAMonitor` BLOB NULL ,
  PRIMARY KEY (`ProcessClass`, `Process_ProcessName`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `MOSESDB2`.`Agreement`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`Agreement` (
  `User_Username` VARCHAR(150) NOT NULL ,
  `SLA_ProcessClass` VARCHAR(150) NOT NULL ,
  `SLA_Process_ProcessName` VARCHAR(150) NOT NULL ,
  `ExpireDate` DATE NULL ,
  `ArrivalRate` DOUBLE NULL ,
  `SLAConstraints` BLOB NULL ,
  `SLAMonitor` BLOB NULL ,
  PRIMARY KEY (`User_Username`, `SLA_ProcessClass`, `SLA_Process_ProcessName`) ,
  INDEX `fk_Agreement_User1` (`User_Username` ASC) ,
  INDEX `fk_Agreement_SLA1` (`SLA_ProcessClass` ASC, `SLA_Process_ProcessName` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `MOSESDB2`.`Group`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`Group` (
  `IDGroup` INT NOT NULL ,
  `SLA_ProcessClass` VARCHAR(150) NOT NULL ,
  `SLA_Process_ProcessName` VARCHAR(150) NOT NULL ,
  `Type` VARCHAR(150) NOT NULL ,
  `Probability` FLOAT NOT NULL ,
  INDEX `fk_Group_SLA1` (`SLA_ProcessClass` ASC, `SLA_Process_ProcessName` ASC) ,
  PRIMARY KEY (`IDGroup`, `SLA_ProcessClass`, `SLA_Process_ProcessName` ) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `MOSESDB2`.`AbstractService`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`AbstractService` (
  `Name` VARCHAR(150) NOT NULL ,
  `NameSpace` VARCHAR(150) NOT NULL ,
  PRIMARY KEY (`Name`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `MOSESDB2`.`ConcreteService`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`ConcreteService` (
  `ID` INT NOT NULL AUTO_INCREMENT ,
  `endpointURL` VARCHAR(150) NOT NULL ,
  `wsdlURL` VARCHAR(150) NOT NULL ,
  `expireDate` DATE NULL ,
  `AbstractService_Name` VARCHAR(150) NOT NULL ,
  `AbstractService_NameSpace` VARCHAR(150) NOT NULL ,
  PRIMARY KEY (`ID`) ,
  INDEX `fk_ConcreteService_AbstractService1` (`AbstractService_Name` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `MOSESDB2`.`AbstractOperation`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`AbstractOperation` (
  `AbstractService_Name` VARCHAR(150) NOT NULL ,
  `Name` VARCHAR(150) NOT NULL ,
  PRIMARY KEY (`AbstractService_Name`, `Name`) ,
  INDEX `fk_AbstarctOperation_AbstractService1` (`AbstractService_Name` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `MOSESDB2`.`ConcreteOperation`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`ConcreteOperation` (
  `ConcreteService_ID` INT NOT NULL ,
  `Name` VARCHAR(150) NOT NULL ,
  `isWorking` TINYINT(1) NULL DEFAULT true ,
  `stateful` TINYINT(1) NULL ,
  `AbstractOperation_AbstractService_Name` VARCHAR(150) NOT NULL ,
  `SLAFile` BLOB NULL ,
  `SLAConstraints` BLOB NULL ,
  `SLAMonitor` BLOB NULL ,
  PRIMARY KEY (`ConcreteService_ID`, `Name`) ,
  INDEX `fk_ConcreteOperation_ConcreteService1` (`ConcreteService_ID` ASC) ,
  INDEX `fk_ConcreteOperation_AbstarctOperation1` (`AbstractOperation_AbstractService_Name` ASC, `Name` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `MOSESDB2`.`Process_has_AbstractService`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`Process_has_AbstractService` (
  `Process_ProcessName` VARCHAR(150) NOT NULL ,
  `AbstractService_Name` VARCHAR(150) NOT NULL ,
  `AverageVisit` FLOAT NULL,
  PRIMARY KEY (`Process_ProcessName`, `AbstractService_Name`) ,
  INDEX `fk_Process_has_AbstractService_Process1` (`Process_ProcessName` ASC) ,
  INDEX `fk_Process_has_AbstractService_AbstractService1` (`AbstractService_Name` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `MOSESDB2`.`ConcreteOperation_has_Group`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`ConcreteOperation_has_Group` (
  `Group_IDGroup` INT NOT NULL ,
  `Group_SLA_ProcessClass` VARCHAR(150) NOT NULL ,
  `Group_SLA_Process_ProcessName` VARCHAR(150) NOT NULL ,
  `ConcreteOperation_ConcreteService_ID` INT NOT NULL ,
  `ConcreteOperation_Name` VARCHAR(150) NOT NULL ,
  PRIMARY KEY (`Group_IDGroup`, `Group_SLA_ProcessClass`, `Group_SLA_Process_ProcessName`,  `ConcreteOperation_ConcreteService_ID`, `ConcreteOperation_Name`) ,
  INDEX `fk_ConcreteOperation_has_Group_Group1` (`Group_IDGroup` ASC, `Group_SLA_ProcessClass` ASC, `Group_SLA_Process_ProcessName` ASC) ,
  INDEX `fk_ConcreteOperation_has_Group_ConcreteOperation1` (`ConcreteOperation_ConcreteService_ID` ASC, `ConcreteOperation_Name` ASC) )
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `MOSESDB2`.`cusum`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`cusum` (
  `ServiceName` VARCHAR(100) NOT NULL,
  `OperationName` VARCHAR(100) NOT NULL,
  `giplus` bigint(20) NOT NULL,
  `giminus` bigint(20) NOT NULL,
  `mui` bigint(20) NOT NULL,
  `Hstar` FLOAT NOT NULL,
  `deviation` FLOAT NOT NULL,
  `K` bigint(20) NOT NULL,
  `flag` int(1)
)
ENGINE = InnoDB;


DROP TABLE IF EXISTS `MOSESDB2`.`InvocationResult`;
CREATE TABLE  `MOSESDB2`.`InvocationResult` (
  `ProcessName` varchar(50) NOT NULL,
  `ServiceName` varchar(50) NOT NULL,
  `OperationName` varchar(50) NOT NULL,
  `ResponseTime` bigint(20) DEFAULT NULL,
  `Response` int(11) NOT NULL,
  `Epoch` int(11) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `MOSESDB2`.`InvocationResult`
--

/*!40000 ALTER TABLE `InvocationResult` DISABLE KEYS */;
LOCK TABLES `InvocationResult` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `InvocationResult` ENABLE KEYS */;


--
-- Definition of table `MOSESDB2`.`LogQoS`
--

DROP TABLE IF EXISTS `MOSESDB2`.`LogQoS`;
CREATE TABLE  `MOSESDB2`.`LogQoS` (
  `ProcessName` varchar(30) NOT NULL,
  `ServiceName` varchar(30) NOT NULL,
  `OperationName` varchar(30) NOT NULL,
  `AvgResponseTime` float NOT NULL,
  `VrcResponseTime` float NOT NULL,
  `Reliability` float NOT NULL,
  `NInterval` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `MOSESDB2`.`LogQoS`
--



--
-- Definition of table `MOSESDB2`.`ModulesRegistration`
--

DROP TABLE IF EXISTS `MOSESDB2`.`ModulesRegistration`;
CREATE TABLE  `MOSESDB2`.`ModulesRegistration` (
  `Address` varchar(50) NOT NULL,
  `ModuleName` varchar(50) NOT NULL,
  UNIQUE KEY `Address` (`Address`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `MOSESDB2`.`ModulesRegistration`
--


--
-- Definition of table `MOSESDB2`.`PerceivedQoS`
--

DROP TABLE IF EXISTS `MOSESDB2`.`PerceivedQoS`;
CREATE TABLE  `MOSESDB2`.`PerceivedQoS` (
  `ProcessName` varchar(30) NOT NULL,
  `ServiceName` varchar(30) NOT NULL,
  `OperationName` varchar(30) NOT NULL,
  `AvgRespTime` float NOT NULL,
  `Reliability` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `MOSESDB2`.`PerceivedQoS`
--



--
-- Definition of table `MOSESDB2`.`Violations`
--

DROP TABLE IF EXISTS `MOSESDB2`.`Violations`;
CREATE TABLE  `MOSESDB2`.`Violations` (
  `ProcessName` varchar(30) NOT NULL,
  `ServiceName` varchar(30) NOT NULL,
  `OperationName` varchar(30) NOT NULL,
  `RespTime` float NOT NULL,
  `Reliability` float NOT NULL,
  `NInterval` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DELIMITER |

CREATE TRIGGER insert_into_SLA
  BEFORE INSERT
  ON SLA
  FOR EACH ROW
  BEGIN
    IF (SELECT COUNT(*) FROM Process WHERE ProcessName=new.Process_ProcessName)= 0
    THEN
      INSERT error_msg VALUES ('Process Foreign Key Constraint Violated for SLA!!!');
    END IF;
  END;
  
|
  
CREATE TRIGGER insert_into_Agreement
  BEFORE INSERT
  ON Agreement
  FOR EACH ROW
  BEGIN
    IF (SELECT COUNT(*) FROM SLA WHERE Process_ProcessName=new.SLA_Process_ProcessName AND ProcessClass=new.SLA_ProcessClass)= 0
    THEN
      INSERT error_msg VALUES ('SLA Foreign Key Constraint Violated for Agreement!!!');
    END IF;    
    IF (SELECT COUNT(*) FROM `User` WHERE Username=new.User_Username)= 0
    THEN
      INSERT error_msg VALUES ('User Foreign Key Constraint Violated for Agreement!!!');  
    END IF;
  END;
  
|
  
CREATE TRIGGER insert_into_Group
  BEFORE INSERT
  ON `Group`
  FOR EACH ROW
  BEGIN
    IF (SELECT COUNT(*) FROM SLA WHERE Process_ProcessName=new.SLA_Process_ProcessName AND ProcessClass=new.SLA_ProcessClass)= 0
    THEN
      INSERT error_msg VALUES ('SLA Foreign Key Constraint Violated for Group!!!');
    END IF;        
  END;
  
|
  
CREATE TRIGGER insert_into_ConcreteService
  BEFORE INSERT
  ON ConcreteService
  FOR EACH ROW
  BEGIN
    IF (SELECT COUNT(*) FROM AbstractService WHERE Name=new.AbstractService_Name)= 0
    THEN
      INSERT error_msg VALUES ('AbstractService Foreign Key Constraint Violated for ConcreteService!!!');
    END IF;        
  END;
  
|
  
CREATE TRIGGER insert_into_AbstractOperation
  BEFORE INSERT
  ON AbstractOperation
  FOR EACH ROW
  BEGIN
    IF (SELECT COUNT(*) FROM AbstractService WHERE Name=new.AbstractService_Name)= 0
    THEN
      INSERT error_msg VALUES ('AbstractService Foreign Key Constraint Violated for AbstractOperation!!!');
    END IF;        
  END;
  
|

CREATE TRIGGER insert_into_ConcreteOperation
  BEFORE INSERT
  ON ConcreteOperation
  FOR EACH ROW
  BEGIN
    IF (SELECT COUNT(*) FROM ConcreteService WHERE ID=new.ConcreteService_ID)= 0
    THEN
      INSERT error_msg VALUES ('ConcreteService Foreign Key Constraint Violated for ConcreteOperation!!!');
    END IF;    
    IF (SELECT COUNT(*) FROM AbstractOperation WHERE Name=new.Name AND AbstractService_Name=new.AbstractOperation_AbstractService_Name)= 0
    THEN
      INSERT error_msg VALUES ('AbstractOperation Foreign Key Constraint Violated for ConcreteOperation!!!');  
    END IF;
  END;
  
|
  
CREATE TRIGGER insert_into_Process_has_AbstractService
  BEFORE INSERT
  ON Process_has_AbstractService
  FOR EACH ROW
  BEGIN
    IF (SELECT COUNT(*) FROM Process WHERE ProcessName=new.Process_ProcessName)= 0
    THEN
      INSERT error_msg VALUES ('Process Foreign Key Constraint Violated for Process_has_AbstractService!!!');
    END IF;    
    IF (SELECT COUNT(*) FROM AbstractService WHERE Name=new.AbstractService_Name)= 0
    THEN
      INSERT error_msg VALUES ('AbstractService Foreign Key Constraint Violated for Process_has_AbstractService!!!');  
    END IF;
  END;
  
|
  
CREATE TRIGGER insert_into_ConcreteOperation_has_Group
  BEFORE INSERT
  ON ConcreteOperation_has_Group
  FOR EACH ROW
  BEGIN
    IF (SELECT COUNT(*) FROM `Group` WHERE IDGroup=new.Group_IDGroup AND SLA_ProcessClass=new.Group_SLA_ProcessClass AND 		
    									 SLA_Process_ProcessName=new.Group_SLA_Process_ProcessName)= 0
    THEN
      INSERT error_msg VALUES ('Group Foreign Key Constraint Violated for ConcreteOperation_has_Group!!!');
    END IF;    
    IF (SELECT COUNT(*) FROM ConcreteOperation WHERE ConcreteService_ID=new.ConcreteOperation_ConcreteService_ID AND Name=new.ConcreteOperation_Name)= 0
    THEN
      INSERT error_msg VALUES ('ConcreteOperation Foreign Key Constraint Violated for ConcreteOperation_has_Group!!!');  
    END IF;
  END;

|
  
CREATE TRIGGER delete_cascade_on_SLA

  BEFORE DELETE
  ON Process
  FOR EACH ROW
  BEGIN
    DELETE FROM SLA
    WHERE Process_ProcessName=old.ProcessName;
  END;
  
|  
  
CREATE TRIGGER delete_cascade_on_Agreemnt_by_User

  BEFORE DELETE
  ON `User`
  FOR EACH ROW
  BEGIN
    DELETE FROM Agreement
    WHERE User_Username=old.Username;
  END;
  
|
  
CREATE TRIGGER delete_cascade_on_Agreement_Group_by_SLA

  BEFORE DELETE
  ON SLA
  FOR EACH ROW
  BEGIN
    DELETE FROM Agreement
    WHERE SLA_ProcessClass=old.ProcessClass AND SLA_Process_ProcessName=old.Process_ProcessName;
    DELETE FROM `Group`
    WHERE SLA_ProcessClass=old.ProcessClass AND SLA_Process_ProcessName=old.Process_ProcessName;
  END;
  
|

CREATE TRIGGER delete_cascade_on_ConcreteOperation_has_Group

  BEFORE DELETE
  ON `Group`
  FOR EACH ROW
  BEGIN
    DELETE FROM ConcreteOperation_has_Group
    WHERE Group_IDGroup=old.IDGroup AND Group_SLA_ProcessClass=old.SLA_ProcessClass AND Group_SLA_Process_ProcessName=old.SLA_Process_ProcessName;
  END;
  
|

delimiter ;


ALTER TABLE `MOSESDB2`.`ConcreteOperation_has_Group` MODIFY COLUMN `Group_IDGroup` INTEGER  DEFAULT NULL,
 MODIFY COLUMN `Group_SLA_ProcessClass` VARCHAR(150)  CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
 MODIFY COLUMN `Group_SLA_Process_ProcessName` VARCHAR(150)  CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
 MODIFY COLUMN `ConcreteOperation_ConcreteService_ID` INTEGER  DEFAULT NULL,
 MODIFY COLUMN `ConcreteOperation_Name` VARCHAR(150)  CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
 ADD COLUMN `id` INT  NOT NULL AUTO_INCREMENT AFTER `ConcreteOperation_Name`,
 DROP PRIMARY KEY,
 ADD PRIMARY KEY (`id`);

ALTER TABLE `MOSESDB2`.`ConcreteOperation` ADD COLUMN `ID` INT  NOT NULL AUTO_INCREMENT AFTER `SLAMonitor`,
 DROP PRIMARY KEY,
-- ADD PRIMARY KEY (`ConcreteService_ID`, `Name`, `ID`);
 ADD PRIMARY KEY (`ID`);

ALTER TABLE `MOSESDB2`.`ConcreteOperation` CHANGE COLUMN `SLAConstraints` `Reliability` DOUBLE  NOT NULL,
 ADD COLUMN `ResponseTime` DOUBLE  NOT NULL AFTER `Reliability`,
 ADD COLUMN `Cost` DOUBLE  NOT NULL AFTER `ResponseTime`,
 ADD COLUMN `RequestRate` DOUBLE  NOT NULL AFTER `Cost`,
 ADD COLUMN `Probability` DOUBLE DEFAULT NULL AFTER `ID`; -- Aggiunta della probabilita che guida il perRequest2

-- Adding solution table for solution caching
create table solution select g.IDGroup, g.Type, g.Probability, c.ConcreteOperation_ConcreteService_ID, s.endpointURL, s.AbstractService_NameSpace, co.Cost, g.SLA_Process_ProcessName, g.SLA_ProcessClass, s.AbstractService_Name, c.ConcreteOperation_Name, c.id from MOSESDB2.Group g, MOSESDB2.ConcreteOperation_has_Group c, MOSESDB2.ConcreteService s, MOSESDB2.ConcreteOperation co WHERE g.Probability>0 AND g.IDGroup=c.Group_IDGroup AND g.SLA_ProcessClass=c.Group_SLA_ProcessClass AND g.SLA_Process_ProcessName=c.Group_SLA_Process_ProcessName AND s.ID=c.ConcreteOperation_ConcreteService_ID AND co.ConcreteService_ID=c.ConcreteOperation_ConcreteService_ID AND co.Name=c.ConcreteOperation_Name;
CREATE INDEX SolutionIndex on solution (SLA_Process_ProcessName,SLA_ProcessClass,AbstractService_Name,ConcreteOperation_Name);
CREATE INDEX idSolution on solution (id);
alter table solution engine=InnoDB;

delete from ConcreteService WHERE endpointURL LIKE '%8086%';
delete from ConcreteService WHERE endpointURL LIKE '%8087%';
delete from ConcreteService WHERE endpointURL LIKE '%8088%';
delete from ConcreteService WHERE endpointURL LIKE '%8089%';
delete from ConcreteService WHERE endpointURL LIKE '%8090%';
delete from ConcreteService WHERE endpointURL LIKE '%8091%';
delete from ConcreteService WHERE endpointURL LIKE '%8092%';
delete from ConcreteService WHERE endpointURL LIKE '%8093%';
delete from ConcreteService WHERE endpointURL LIKE '%8094%';
delete from ConcreteOperation WHERE ConcreteService_ID NOT IN (SELECT ID FROM ConcreteService);


-- -----------------------------------------------------
-- Table `MOSESDB2`.`ProcessPath`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`ProcessPath` (
  `ProcessName` VARCHAR(150) NOT NULL ,
  `ProcessPathID` INT NOT NULL AUTO_INCREMENT ,
  `Probability` DOUBLE NOT NULL, 
  PRIMARY KEY (`ProcessPathID`),
  FOREIGN KEY (ProcessName) REFERENCES Process(ProcessName)
 )
ENGINE = MyISAM;

-- -----------------------------------------------------
-- Table `MOSESDB2`.`ServiceOccurrence`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`ServiceOccurrence` (
  `AbstractService` VARCHAR(150) NOT NULL ,
  `ProcessName` VARCHAR(150) NOT NULL ,
  `ProcessPathID` INT NOT NULL,
  `Count` INT NOT NULL,
  PRIMARY KEY (`AbstractService`, `ProcessName`, `ProcessPathID`, `Count`),
  FOREIGN KEY (ProcessName) REFERENCES Process(ProcessName),
  FOREIGN KEY (ProcessPathID) REFERENCES ProcessPath(ProcessPathID),
  FOREIGN KEY (AbstractService) REFERENCES AbstractService(Name)
 )
ENGINE = MyISAM;

-- -----------------------------------------------------
-- Table `MOSESDB2`.`ProcessSubPath`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`ProcessSubPath` (
  `ProcessPathID` INT NOT NULL ,
  `SeqNumber` INT NOT NULL ,
  `ProcessSubPathID` INT NOT NULL AUTO_INCREMENT,
  `AbstractServiceName` VARCHAR(150) NOT NULL,
  PRIMARY KEY (ProcessPathID,SeqNumber,ProcessSubPathID,AbstractServiceName),
  FOREIGN KEY (ProcessPathID) REFERENCES ProcessPath(ProcessPathID),
  FOREIGN KEY (AbstractServiceName) REFERENCES AbstractService(Name)
 )
ENGINE = MyISAM;

-- -----------------------------------------------------
-- Table `MOSESDB2`.`NumRequest`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`NumRequest` (
  `ProcessName` VARCHAR(150) NOT NULL ,
  `RequestID` INT NOT NULL AUTO_INCREMENT ,  
  PRIMARY KEY (`RequestID`),
  FOREIGN KEY (ProcessName) REFERENCES Process(ProcessName)
 )
ENGINE = MyISAM;

-- -----------------------------------------------------
-- Table `MOSESDB2`.`NumRequest`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MOSESDB2`.`N` (
  `ServiceName` INT(11) NOT NULL,
  `OperationName` VARCHAR(150) NOT NULL,
  `Nplus` INT(11) NOT NULL DEFAULT 0,
  `Nminus` INT NOT NULL DEFAULT 0
 )
ENGINE = MyISAM;



DELIMITER |
CREATE TRIGGER delete_subpaths
  BEFORE DELETE
  ON ProcessPath
  FOR EACH ROW
  BEGIN
    DELETE FROM ProcessSubPath WHERE ProcessPathID=old.ProcessPathID;
  END;
|

CREATE TRIGGER cusum
  BEFORE INSERT
  ON InvocationResult
  FOR EACH ROW
  BEGIN
	DECLARE epoch int(11);
	CALL cusum(new.ServiceName,new.OperationName,new.ResponseTime,epoch);
	SET NEW.Epoch=epoch;
	
  END;
|

DELIMITER ;

ALTER TABLE `MOSESDB2`.`solution` ADD COLUMN `RequestID` VARCHAR(255)  DEFAULT '' AFTER `id`;
CREATE INDEX RequestID on solution(RequestID);

INSERT INTO N (Nplus,Nminus) VALUES (1,1);

DELIMITER //   
CREATE PROCEDURE `cusum` (IN SN VARCHAR(100), IN OpN VARCHAR(100), IN rt BIGINT(20), OUT newEpoch INT(11))  
LANGUAGE SQL  
DETERMINISTIC  
SQL SECURITY DEFINER  
COMMENT 'Cusum Procedure'  
BEGIN  
	#Initial declarations
	IF (OpN = 's1op') THEN
		SET @delta=500;
	ELSEIF (OpN = 's2op') THEN
		SET @delta=250;
	ELSEIF (OpN = 's3op') THEN
		SET @delta=250;
	ELSEIF (OpN = 's4op') THEN
		SET @delta=125;
	ELSEIF (OpN = 's5op') THEN
		SET @delta=500;
	ELSEIF (OpN = 's6op') THEN
		SET @delta=450;
	END IF;
	SET @k=@delta/2;
	SET @giplus=0;
	SET @giminus=0;
	SET @mui=0;
	SET @hstar=0;
	SET @deviation=0;
	SET @Nplus=1;
	SET @Nminus=1;
	SET @alpha=0.5;
	SET @flag=0;

	#Verifying whether it is the first insertion
	SELECT COUNT(*) INTO @firstInsert FROM cusum WHERE ServiceName=SN AND OperationName=OpN;
	
	#Computing @giplus e @giminus
	IF (@firstInsert = 0) THEN
		#If @firstInsert=0 the operation has never been inserted
		SELECT GREATEST(0, rt-@k) INTO @giplus;		
		SET @giminus=0;
		INSERT INTO N (ServiceName, OperationName, Nplus, Nminus) VALUES (SN, OpN, 1, 1);
	ELSE
		SELECT giplus,giminus,mui into @giplus,@giminus,@mui FROM cusum WHERE ServiceName=SN AND OperationName=OpN;
		SELECT GREATEST(0, @giplus+rt-@mui-@k) INTO @giplus;
		SELECT GREATEST(0, @giminus+@mui-@k-rt) INTO @giminus;
	END IF;

	#Computing @hstar
	SELECT deviation INTO @deviation FROM cusum WHERE ServiceName=SN AND OperationName=OpN;
        SET @hstar=-1.3645*pow(@deviation,3)/pow(@delta,3)+10.3031*pow(@deviation,2)/pow(@delta,2)+3.1860*@deviation/@delta-0.2882;
	
	#Verying thresholds and computing updated @mui
        SELECT Nminus,Nplus INTO @Nminus,@Nplus FROM N WHERE ServiceName=SN AND OperationName=OpN;
	IF (@giplus>@hstar) THEN
		SET @mui=@mui+@k+@giplus/@Nplus;
		UPDATE N SET Nplus=1 WHERE ServiceName=SN AND OperationName=OpN;
		SET @flag=1;
	ELSE
		SET @mui=@alpha*rt+(1-@alpha)*@mui;
		SET @Nplus=@Nplus+1;
		UPDATE N SET Nplus=@Nplus WHERE ServiceName=SN AND OperationName=OpN;
	END IF;
	IF (@giminus>@hstar) THEN
		SET @mui=@mui-@k-@giminus/@Nminus;
		SELECT GREATEST(0, @mui) INTO @mui;
		UPDATE N SET Nminus=1 WHERE ServiceName=SN AND OperationName=OpN;
		SET @flag=1;
	ELSE
		SET @mui=@alpha*rt+(1-@alpha)*@mui;
		SET @Nminus=@Nminus+1;
		UPDATE N SET Nminus=@Nminus WHERE ServiceName=SN AND OperationName=OpN;
	END IF;	

	#Updating deviation
	SET @deviation=@alpha*ABS(rt-@mui)+(1-@alpha)*@deviation;

	#Data insertion
	IF (@firstInsert=0) THEN
	INSERT INTO cusum (ServiceName, OperationName, giplus, giminus, mui, Hstar, deviation, K)
		VALUES(SN,OpN, @giplus, @giminus, @mui, @hstar, @deviation, @k);
	ELSE
		UPDATE cusum SET giplus=@giplus, giminus=@giminus, mui=@mui, hstar=@hstar, deviation=@deviation, k=@k
			WHERE ServiceName=SN AND OperationName=OpN;
	END IF;

	IF (@flag=1) THEN
		SELECT COUNT(*) INTO @firstStatistic FROM PerceivedQoS WHERE ServiceName=SN AND OperationName=OpN;
		IF (@firstStatistic = 0) THEN
			INSERT INTO PerceivedQoS (ProcessName, ServiceName, OperationName, AvgRespTime, Reliability)
				VALUES ('ESECProcess', SN, OpN, @mui/1000, 1);
		ELSE
			UPDATE PerceivedQoS SET AvgRespTime=@mui/1000 WHERE ServiceName=SN AND OperationName=OpN;
		END IF;
	END IF;
END

