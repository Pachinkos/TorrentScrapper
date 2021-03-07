package models;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.time.Instant;

/**
 * Base domain object with Id, version, whenCreated and whenModified.
 */
@MappedSuperclass
public abstract class BaseModel extends Model {

  @Id
  protected long id;

  @Version
  protected Long version;

  @WhenCreated
  protected Instant creation;

  @WhenModified
  protected Instant modification;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public Instant getCreation() {
    return creation;
  }

  public void setCreation(Instant creation) {
    this.creation = creation;
  }

  public Instant getModification() {
    return modification;
  }

  public void setModification(Instant modification) {
    this.modification = modification;
  }

}
