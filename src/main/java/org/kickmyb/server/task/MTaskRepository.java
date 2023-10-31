package org.kickmyb.server.task;

import org.kickmyb.server.account.MUser;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MTaskRepository extends PagingAndSortingRepository<MTask, Long> {
}
