package com.neu.liveNodeList;

import com.neu.node.Node;
import io.netty.channel.Channel;

/**
 * Store current live nodes metadata with the ascending order of the user id.
 * Client side list will store io channel, server side list will not.
 */
public interface LiveNodeList <E extends Comparable<E>> {

    /**
     * Add a new node to the list.
     *
     * @param node the node to be added
     * @return true if the node is successfully added, otherwise false due to duplicated nodes
     */
    boolean add(E node);

    /**
     * Remove the node by its user id. If the node associates with a channel, this will close the channel as well.
     *
     * @param id the user id
     * @return true if the channel has been removed, false if the channel doesn't exist
     */
    boolean remove(Long id);

    /**
     * Query the node channel by provided channel.
     *
     * @param id the user id
     * @return true if the user with the id is present, otherwise false
     */
    boolean isContain(Long id);

    /**
     * Get the node channel by the id of the user of the node
     *
     * @return
     */
    E get(Long id);

    E getLeaderNode();

    Iterable<E> getAllNodes();

    int size();



}
