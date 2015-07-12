/* 
 * Copyright 2013-2015 JIWHIZ Consulting Inc.
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
package nebhistory.repository;

import java.util.List;
import java.util.Optional;

import nebhistory.model.HistoryMessage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface HistoryMessageRepository extends PagingAndSortingRepository<HistoryMessage, Long>{
    
	List<HistoryMessage> findByUserId(Long userId);
	
    Page<HistoryMessage> findOrderByCreatedDate(Pageable pageable);

	Optional<HistoryMessage> findOneById(Long historyMessageId);
	
	Optional<HistoryMessage> findOneByUserId(Long userID);
}	