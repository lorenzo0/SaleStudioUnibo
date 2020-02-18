<?php

class SS {
   public $name;
    
   public $latitude;
   public $longitude;
   
   public $totalSeats;
   public $occupiedSeats;
   
   public $dayOfTheWeek;
   public $openingHour;
   public $closingHour;

    function setName($name){ 
         $this->name = $name; 
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

$SaleStudioArray = array();

$sql = "SELECT `name`, `latitude`, `longitude`, `totalSeats`, `occupiedSeats`, `dayOfTheWeek`, `openingHour`, `closingHour` 
        FROM `StudyRooms`
        INNER JOIN Rooms ON StudyRooms.IDStudyRoom = Rooms.IDStudyRoom
        INNER JOIN OpeningHour ON StudyRooms.IDStudyRoom = OpeningHour.IDStudyRoom";
        
$result = $conn->query($sql);

//$output['SalaStudio'] = array();
$json = ['SalaStudio' => []];

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        
        /*$name = $row["name"];
        
        $latitude = $row["latitude"];
        $longitude = $row["longitude"];
        
        $totalSeats = $row["totalSeats"];
        $occupiedSeats = $row["occupiedSeats"];
        
        $dayOfTheWeek = $row["dayOfTheWeek"];
        $openingHour = $row["openingHour"];
        $closingHour = $row["closingHour"];
        
        $ss = new SS($name, $latitude, $longitude, $totalSeats, $occupiedSeats, $dayOfTheWeek, $openingHour, $closingHour);
        
        $ss->setName($name);
        $ss->setLatitude($latitude);
        $ss->setLongitude($longitude);
        $ss->setTotalSeats($totalSeats);
        $ss->setOccupiedSeats($occupiedSeats);
        $ss->setDayOfTheWeek($dayOfTheWeek);
        $ss->setOpeningHour($openingHour);
        $ss->setClosingHour($closingHour);
        
        //$output['SalaStudio'][] = array($ss);
        
        jsonData = json_encode('SalaStudio'=> $ss);
        echo $jsonData."\n";*/
        
        $json['SalaStudio'][] = [
            'name' => $row['name'],
            'latitude' => $row['latitude'],
            'longitude' => $row['longitude'],
            'totalSeats' => $row['totalSeats'],
            'occupiedSeats' => $row['occupiedSeats'],
            'dayOfTheWeek' => $row['dayOfTheWeek'],
            'openingHour' => $row['openingHour'],
            'closingHour' => $row['closingHour']
        ];
    }
    
    echo json_encode($json);
    
    //echo json_encode( $output );
} else {
    echo "0 results";
}

$conn->close();

?>