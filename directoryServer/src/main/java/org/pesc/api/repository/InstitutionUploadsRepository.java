/*
 * Copyright (c) 2017. California Community Colleges Technology Center
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

package org.pesc.api.repository;

import org.pesc.api.model.InstitutionsUpload;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/21/16.
 */
@Repository
public interface InstitutionUploadsRepository extends CrudRepository<InstitutionsUpload, Integer> {

    @Query("from InstitutionsUpload where userId = ?1")
    List<InstitutionsUpload> findByUserId(Integer userID);

    @Query("from InstitutionsUpload where organizationId = ?1")
    List<InstitutionsUpload> findByOrgId(Integer orgID);

}
