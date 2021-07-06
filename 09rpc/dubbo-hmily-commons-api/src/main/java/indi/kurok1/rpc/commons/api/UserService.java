package indi.kurok1.rpc.commons.api;

import indi.kurok1.rpc.commons.domain.User;

/**
 * TODO
 *
 * @author <a href="mailto:chan@ittx.com.cn">韩超</a>
 * @version 2021.07.06
 */
public interface UserService {

    User getById(Long id);

}
