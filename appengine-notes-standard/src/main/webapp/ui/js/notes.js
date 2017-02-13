angular.module('notes', ['ngMaterial'])
.controller(
		'NotesController',
		[
				'$scope', '$http', '$timeout', '$mdDialog',
				function($scope, $http, $timeout, $mdDialog) {
					$scope.initialized = "...";

					$scope.load = function load() {
						$scope.notes = [];
						$scope.initialized = "INIT complete";
						
						$http.get("../api/notes").success(
								function(data, status, headers, config) {
									$scope.initialized = "yes";
									$scope.notes = data;
									console.log("found "+data.length+" notes");
								}).error(
								function(data, status, headers, config) {
								});
					};


					$scope.add = function add(event) {
						console.log("ADD");
						
						var $outerScope = $scope;
						
						$mdDialog.show({
					      controller: function DialogController($scope, $mdDialog) {
								$scope.located = function located(position) {
									console.log(position);
									$scope.latitude = position.coords.latitude;
									$scope.longitude = position.coords.longitude;
									$scope.$apply();
								}

							    if (navigator.geolocation) {
							        navigator.geolocation.getCurrentPosition($scope.located);
							    } else {
									$scope.latitude = 0.0;
									$scope.longitude = 0.0;
							    }
								
					            $scope.cancel = function() {
					                $mdDialog.hide();
					              };
					              
					            $scope.save = function() {
					                console.log("SAVE "+$scope.textLong);
					                
					                var note = {
					                	text: $scope.textLong
					                	, latitude: $scope.latitude
					                	, longitude: $scope.longitude
					                }
					                
									$http.post("../api/notes", note).success(function(data, status, headers, config) {
											$outerScope.notes.unshift(data);
											$mdDialog.hide();
										}).error(function(data, status, headers, config) {
											console.log("Error: "+status);
											$mdDialog.hide();
											$mdDialog.show($mdDialog.alert( {
													title: "Error",
													textContent: "Unable to add note, status="+status+"!"
											}));
										});

					                
					              }
					            },
					      templateUrl: 'templates/noteAdd.html',
					      parent: angular.element(document.body),
					      targetEvent: event,
					      clickOutsideToClose:false
					    });
						
					}; // function add(...)
					
					$scope.details = function details(event, note) {
						console.log("details ");
						console.log(note);
						
						$http.get("../api/google/translate", { params: { message: note.text , lang: navigator.language }}).success(
								function(translationData, status, headers, config) {
									$http.get("../api/google/locate", { params: { latitude: note.latitude , longitude: note.longitude }}).success(
											function(locationData, status, headers, config) {
												$mdDialog.show({
												      controller: function DialogController($scope, $mdDialog) {
												    	    $scope.address = "Unknown";
												    	    if (locationData && locationData.address)
												    	    	$scope.address = locationData.address;
												    	    $scope.textOriginal = translationData.textOriginal;
												    	    $scope.textTranslated = translationData.textTranslated;
												    	    $scope.showTranslation = (translationData.textTranslated != translationData.textOriginal);
												    	  
												            $scope.cancel = function() {
												                $mdDialog.hide();
												              };
												      },
												      templateUrl: 'templates/noteDetails.html',
												      targetEvent: event,
												      clickOutsideToClose:true
												    });
											}).error(function(data, status, headers, config) {
												$mdDialog.show($mdDialog.alert( {
													title: "Error",
													textContent: "Unable to locate note origin, status="+status+"!"
												}));
											});
								}).error(
								function(data, status, headers, config) {
									$mdDialog.show($mdDialog.alert( {
										title: "Error",
										textContent: "Unable to translate message, status="+status+"!"
									}));
								});
					}; // function details(..)
						
					$scope.testScaling = function testScaling(event) {
						console.log("Test scaling");
						
						var $outerScope = $scope;
						
						$mdDialog.show({
					      controller: function DialogController($scope, $mdDialog) {
					    	    $scope.primes = 10000;
					    	    $scope.requests = 5;

				                $scope.active = 0;
				                $scope.errors = 0;
				                $scope.success = 0;
				                
					            $scope.cancel = function() {
					                $mdDialog.hide();
					              };
					              
					            $scope.calculate = function() {
					                console.log("Calculate "+$scope.requests+" * "+$scope.primes);
					                
					                for (var i=0; i<$scope.requests; i++) {
					                	$scope.active++;
										$http.get("../api/primes", { params : { count : $scope.primes }}).success(function(data, status, headers, config) {
												$scope.success = $scope.success + 1;
												$scope.active--;
												console.log("SUCCESS succ="+$scope.success+" error="+$scope.errors);
											}).error(function(data, status, headers, config) {
												$scope.errors = $scope.errors + 1;
												$scope.active--;
												console.log("ERROR   succ="+$scope.success+" error="+$scope.errors);
											});
					                }
					                console.log("Started "+$scope.requests+" requests");
					      		}
					      },
					      templateUrl: 'templates/primesForm.html',
					      parent: angular.element(document.body),
					      targetEvent: event,
					      clickOutsideToClose:false
					    });
						
					}; // function testScaling(..)
						
				} // function scope(...)
			]); // .controller(
