<?php

class SS {
   public $ID;
   public $name;
   public $addressStreet;
   public $addressNumber;
   public $latitude;
   public $longitude;
   public $totalSeats;
   public $occupiedSeats;
   public $dayOfTheWeek;
   public $openingHour;
   public $closingHour;

    
    function setID($ID){ 
         $this->ID = $ID; 
    }
    
    function setName($name){ 
         $this->name = $name; 
    }
    
    function setAddressStreet($addressStreet){ 
         $this->addressStreet = $addressStreet; 
    }
    
    function setAddressNumber($addressNumber){ 
         $this->addressNumber = $addressNumber; 
    }
    
    function setLatitude($latitude){ 
         $this->latitude = $latitude; 
    }
    
    function setLongitude($longitude){ 
         $this->longitude = $longitude; 
    }
    
    function setTotalSeats($totalSeats){ 
         $this->totalSeats = $totalSeats; 
    }
    
    function setOccupiedSeats($occupiedSeats){ 
         $this->occupiedSeats = $occupiedSeats; 
    }
    
    function setDayOfTheWeek($dayOfTheWeek){ 
         $this->dayOfTheWeek = $dayOfTheWeek;
    }
    
    function setOpeningHour($openingHour){ 
         $this->openingHour = $openingHour; 
    }
    
    function setClosingHour($closingHour){ 
         $this->closingHour = $closingHour; 
    }
    
    
}

$servername = "localhost";
$username = "aulestudiounibo";
$password = "fQ9j9ZdQyTDC";
$dbname = "my_aulestudiounibo";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$newId = $_GET['IDStudyRoom'];

$sql = "SELECT * 
		FROM StudyRooms
        INNER JOIN Rooms ON StudyRooms.IDStudyRoom = Rooms.IDStudyRoom
        INNER JOIN OpeningHour ON StudyRooms.IDStudyRoom = OpeningHour.IDStudyRoom
        WHERE StudyRooms.IDStudyRoom='$newId'";
        
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        $ID = $newId;
        $name = $row["name"];
        $addressStreet = $row["addressStreet"];
        $addressNumber = $row["addressNumber"];
        $latitude = $row["latitude"];
        $longitude = $row["longitude"];
        
        
        $totalSeats = $row["totalSeats"];
        $occupiedSeats = $row["occupiedSeats"];
        
        $dayOfTheWeek = $row["dayOfTheWeek"];
        $openingHour = $row["openingHour"];
        $closingHour = $row["closingHour"];
        
        $ss = new SS($ID, $name, $addressStreet, $addressNumber, $latitude, $longitude,
        			$totalSeats, $occupiedSeats, $dayOfTheWeek, $openingHour, $closingHour);
        
        
        $ss->setID($ID);
        $ss->setName($name);
        $ss->setAddressStreet($addressStreet);
        $ss->setAddressNumber($addressNumber);
        $ss->setLatitude($latitude);
        $ss->setLongitude($longitude);
        $ss->setTotalSeats($totalSeats);
        $ss->setOccupiedSeats($occupiedSeats);
        $ss->setDayOfTheWeek($dayOfTheWeek);
        $ss->setOpeningHour($openingHour);
        $ss->setClosingHour($closingHour);
        
        $jsonData = json_encode(['SalaStudio' => $ss]);
        echo $jsonData."\n";
        
    }
} else {
    echo "0 results";
}

$conn->close();

?>