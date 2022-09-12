package com.company.Graphs;

import com.company.Graphs.Algorithms.ConnectionCheckGraphAlgorithm;
import com.company.Graphs.Algorithms.GraphAlgorithmInterface;
import com.company.Graphs.Algorithms.ShortestDistanceFromVertexCalculationGraphAlgorithm;
import com.company.Graphs.Errors.EdgeAlreadyExistsException;
import com.company.Graphs.Errors.NoSuchVertexException;
import com.company.Graphs.Errors.VertexAlreadyExistsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @param <T> Type of vertexId
 * @param <E> Type of values in vertex
 */
public abstract class AbstractGraph<T, E> implements Graph<T, E> {
    protected Map<T, List<T>> connectionsMap = new HashMap<>();
    protected Map<T, E> vertexValuesMap = new HashMap<>();

    /**
     * Adds a new vertex with a specific id and value
     *
     * @param vertexId id of a new vertex
     * @param value    value of a new vertex
     * @throws VertexAlreadyExistsException if a vertex with a specified id already exists
     */
    public void addVertex(T vertexId, E value) throws VertexAlreadyExistsException {
        if (vertexValuesMap.containsKey(vertexId))
            throw new VertexAlreadyExistsException("Vertex " + vertexId + " already exists");
        vertexValuesMap.put(vertexId, value);
        connectionsMap.put(vertexId, new ArrayList<>());
    }

    /**
     * Removes a vertex with a specific id and value.
     * In addition, it deletes all edges to and from a vertex
     *
     * @param vertexId id of a vertex to delete
     * @throws NoSuchVertexException if a vertex with a specified id doesn't exist
     */
    public void removeVertex(T vertexId) throws NoSuchVertexException {
        if (!connectionsMap.containsKey(vertexId))
            throw new NoSuchVertexException("There is no such vertex " + vertexId);
        vertexValuesMap.remove(vertexId);
        connectionsMap.remove(vertexId);
        for (List<T> list : connectionsMap.values()) {
            list.remove(vertexId);
        }
    }

    /**
     * @param vertexId id of a vertex
     * @return value of a vertex with a specified id
     * @throws NoSuchVertexException if a vertex with a specified id doesn't exist
     */
    public E getVertexValue(T vertexId) throws NoSuchVertexException {
        if (!connectionsMap.containsKey(vertexId))
            throw new NoSuchVertexException("There is no such vertex " + vertexId);
        return vertexValuesMap.get(vertexId);
    }

    /**
     * Allows to run a specific algorithm on a grapht
     *
     * @param algorithm algorithm you want to run
     * @param <P>       type of result
     * @return returns result of an execution of the algorithm
     */
    @Override
    public <P> P runAlgorithm(GraphAlgorithmInterface<P, T, E> algorithm) {
        return algorithm.run(this);
    }

    /**
     * @return list of all ids of vertexes in a graph
     */
    @Override
    public List<T> getAllVertexesIds() {
        return new ArrayList<>(vertexValuesMap.keySet());
    }

    /**
     * Connects a specified vertex with all not yet connected vertexes
     *
     * @param vertexId id of a vertex
     * @throws NoSuchVertexException if a specified vertex doesn't exist
     */
    @Override
    public void connectVertexWithNotDirectlyConnectedVertexes(T vertexId) throws NoSuchVertexException, EdgeAlreadyExistsException {
        if (!connectionsMap.containsKey(vertexId))
            throw new NoSuchVertexException("There is no such vertex " + vertexId);
        for (Map.Entry<T, List<T>> entry : connectionsMap.entrySet()) {
            if (entry.getKey().equals(vertexId)) continue;
            if (connectionsMap.get(vertexId).contains(entry.getKey())) continue;
            addEdge(vertexId, entry.getKey());
        }
    }

    /**
     * Checks if graph is connected
     *
     * @return true if graph is connected and false otherwise
     */
    @Override
    public boolean isGraphConnected() {
        return runAlgorithm(new ConnectionCheckGraphAlgorithm<>());
    }

    /**
     * Counts the shortest distance between two vertexes (considers each vertex of the same length)
     *
     * @param firstVertex  id of a first vertex
     * @param secondVertex id of a second vertex
     * @return if there is a path from firstVertex to secondVertex returns distance between them
     * otherwise 2147483647 (2^31 - 1)
     * @throws NoSuchVertexException
     */
    @Override
    public Integer calculateShortestDistanceBetweenVertexes(T firstVertex, T secondVertex) throws NoSuchVertexException {
        if (!connectionsMap.containsKey(firstVertex))
            throw new NoSuchVertexException("There is no such vertex " + firstVertex);
        if (!connectionsMap.containsKey(secondVertex))
            throw new NoSuchVertexException("There is no such vertex " + secondVertex);
        return runAlgorithm(new ShortestDistanceFromVertexCalculationGraphAlgorithm<>(firstVertex)).get(secondVertex);
    }

    /**
     * @return number of vertexes in a graph
     */
    @Override
    public int getVertexNumber() {
        return vertexValuesMap.size();
    }

    /**
     * @return number of edges in a graph
     */
    @Override
    public int getEdgesNumber() {
        return connectionsMap.values().stream().mapToInt(List::size).sum();
    }
}
