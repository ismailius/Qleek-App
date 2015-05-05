angular.module("myApp.controllers", ['ngRoute', 'ngDialog'])

.controller('homeCtrl', function($scope, $http, ngDialog, $location, $rootScope) 
{
	
	$scope.nok = false
	$scope.ok = false
	$scope.message = false

	$http.get('/wifi_list')
	.then(function(res){
		var wifiList = []


		for (var i = res.data.length - 1; i >= 0; i--) {
			wifiList.push({ ssid: res.data[i][0], security: res.data[i][1] })
		};

		$scope.wifis = wifiList
	});

	$rootScope.$on('connecting', function(event, user) 
    { 
    	$scope.step = true
    })


	$scope.openDialog = function(wifi) 
	{
		var dialog = ngDialog.open({
			template: 'popupWifi.html',
			controller: ['$scope', function($scope) {
				$scope.selected = wifi

				if ($scope.selected.security == "NONE") 
				{
					$scope.secure = false;
				}
				else
				{
					if($scope.selected.security == "WEP")
					{
						$scope.min_length= 6;
					}
					else{
						$scope.min_length= 8;	
					}
					$scope.secure = true;
				}

			}] 
		})

		dialog.closePromise.then(function (data) {
			// console.log(data)
			// if (true) {};
			// $scope.step = true
		})
	}




	$scope.blink = function (bool) {
		$scope.message = true

		if (bool) 
		{
			$scope.ok = true
			$scope.nok = false
		}
		else
		{
			$scope.ok = false
			$scope.nok = true
		}

	}

	$scope.reloadPage = function () {
		location.reload();
	}


	$scope.connectOpen = function (wifi) {
		// console.log($scope)

		 $rootScope.$broadcast('connecting', true) 
		ngDialog.close()

		$http({
			method: 'POST',
			url: "/wifi",
			data: $.param({ wifi : wifi, password : "none"}),
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})	
}

$scope.connect = function (wifi, password) {

	$scope.step = true
 	$rootScope.$broadcast('connecting', true) 
	ngDialog.close()

		$http({
			method: 'POST',
			url: "/wifi",
			data: $.param({ wifi : wifi, password : password}),
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
}			

})
