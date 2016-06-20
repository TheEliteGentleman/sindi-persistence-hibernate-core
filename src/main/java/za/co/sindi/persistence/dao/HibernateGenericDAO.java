/**
 * 
 */
package za.co.sindi.persistence.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import za.co.sindi.persistence.entity.Entity;
import za.co.sindi.persistence.exception.DAOException;

/**
 * @author Bienfait Sindi
 * @since 07 July 2010
 *
 */
public abstract class HibernateGenericDAO<T extends Entity<PK>, PK extends Serializable> extends AbstractGenericDAO<T, PK> {

	private static final String NULL_SESSION = "No Hibernate Session provided.";
	
	private Session session;
	protected Class<T> persistentClass;
	
	/**
	 * @param persistentClass
	 */
	protected HibernateGenericDAO(Session session, Class<T> persistentClass) {
		super();
		this.session = session;
		this.persistentClass = persistentClass;
	}

	/**
	 * @return the session
	 */
	protected Session getSession() {
		return session;
	}

	/* (non-Javadoc)
	 * @see com.neurologic.music4point0.dao.GenericDAO#delete(com.neurologic.music4point0.entity.base.Entity)
	 */
	public boolean delete(T entity) throws DAOException {
		// TODO Auto-generated method stub
		if (session == null) {
			throw new DAOException(NULL_SESSION);
		}
		
		boolean deleted = false;
		try {
			session.delete(entity);
			deleted = true;
		} catch (HibernateException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new DAOException(e);
		}
		
		return deleted;
	}

	/* (non-Javadoc)
	 * @see za.co.sindi.persistence.dao.GenericDAO#delete(java.io.Serializable)
	 */
	public boolean delete(PK id) throws DAOException {
		// TODO Auto-generated method stub
		if (session == null) {
			throw new DAOException(NULL_SESSION);
		}
		
		boolean deleted = false;
		try {
			T instance = find(id);
			if (instance != null) {
				session.delete(instance);
				deleted = true;
			}
		} catch (HibernateException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new DAOException(e);
		}
		
		return deleted;
	}

	/* (non-Javadoc)
	 * @see za.co.sindi.persistence.dao.GenericDAO#findAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> findAll() throws DAOException {
		// TODO Auto-generated method stub
		if (session == null) {
			throw new DAOException(NULL_SESSION);
		}
		
		return session.createQuery("FROM " + persistentClass.getName()).list();
	}

	/* (non-Javadoc)
	 * @see za.co.sindi.persistence.dao.GenericDAO#find(java.io.Serializable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T find(PK id) throws DAOException {
		// TODO Auto-generated method stub
		if (session == null) {
			throw new DAOException(NULL_SESSION);
		}
		
		T value = null;
		try {
			value = (T) session.get(persistentClass, id);
		} catch (HibernateException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new DAOException(e);
		}
		
		return value;
	}

	/* (non-Javadoc)
	 * @see com.neurologic.music4point0.dao.GenericDAO#insert(com.neurologic.music4point0.entity.base.Entity)
	 */
	public boolean save(T entity) throws DAOException {
		// TODO Auto-generated method stub
		if (session == null) {
			throw new DAOException(NULL_SESSION);
		}
		
		boolean inserted = true;
		try {
			session.save(entity);
		} catch (HibernateException e) {
			inserted = false;
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new DAOException(e);
		}
		
		return inserted;
	}

	/* (non-Javadoc)
	 * @see com.neurologic.music4point0.dao.GenericDAO#update(com.neurologic.music4point0.entity.base.Entity)
	 */
	public boolean update(T entity) throws DAOException {
		// TODO Auto-generated method stub
		if (session == null) {
			throw new DAOException(NULL_SESSION);
		}
		
		if (!entity.isNew()) {
			throw new DAOException("Unable to update a new entity.");
		}
		
		boolean updated = false;
		try {
			session.update(entity);
			updated = true;
		} catch (HibernateException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new DAOException(e);
		}
		
		return updated;
	}

	@SuppressWarnings("unchecked")
	public Collection<T> search(Criterion criterion) throws DAOException {
		if (criterion == null) {
			throw new IllegalArgumentException("Null criterion provided.");
		}
		
		if (session == null) {
			throw new DAOException(NULL_SESSION);
		}
		
		Criteria criteria = session.createCriteria(persistentClass);
		criteria.add(criterion);
		return (List<T>)criteria.list();
	}
}
