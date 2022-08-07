package com.neu.liveNodeList;

import io.netty.channel.Channel;

/**
 * Store current live nodes metadata.
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
     * Remove the node by its user id.
     *
     * @param id the user id
     * @return true if the channel has been removed, false if the channel doesn't exist
     */
    boolean remove(Long id);

    /**
     * Query the node channel by provided channel.
     *
     * @param channel the io channel to be queried
     * @return true if the channel is present, otherwise false
     */
    boolean isContain(Channel channel);

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
