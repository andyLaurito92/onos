/*
 * Copyright 2015 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 ONOS GUI -- Flow View Module
 */

(function () {
    'use strict';

    // injected references
    var $log, $scope, $location, fs, ts, tbs;

    angular.module('ovFlow', [])
    .controller('OvFlowCtrl',
        ['$log', '$scope', '$location',
            'FnService', 'TableService', 'TableBuilderService',

        function (_$log_, _$scope_, _$location_, _fs_, _ts_, _tbs_) {
            var self = this,
                params;
            $log = _$log_;
            $scope = _$scope_;
            $location = _$location_;
            fs = _fs_;
            ts = _ts_;
            tbs = _tbs_;

            params = $location.search();
            if (params.hasOwnProperty('devId')) {
                self.devId = params['devId'];
            }

            tbs.buildTable({
                self: self,
                scope: $scope,
                tag: 'flow',
                query: params
            });

            $scope.refresh = function () {
                $log.debug('Refreshing flows page');
                ts.resetSortIcons();
                $scope.sortCallback();
            };
            
            $log.log('OvFlowCtrl has been created');
        }]);
}());
