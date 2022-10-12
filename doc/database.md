# Database

## user
```json
user
{
  "_id": <objectId>,
  "username": <string>,
  "password": <string>,
  "legal_name": <string>,
  "nickname": <string>,
  "gender": <string>,
  "birthdate": <date>,
  "roles": <array>,
  "contact": <object>,
  "team": <object>,
  "profile": <objectId>,
  "notification": <array>,
  "rating": <double>,
  "is_deactivated": <bool>,
  "created_date": <timestamp>,
  "modified_date": <timestamp>,
  "schema_version": <string>
}

contact
{
  "_id": <objectId>,
  "email": <string>,
  "verification_code": <string>,
  "is_verified": <bool>,
  "is_registered": <bool>,
  "created_date": <timestamp>,
  "modified_date": <timestamp>,
  "schema_version": <string>
}

team
{
  "_id": <objectId>,
  "current_team": <objectId>,
  "previous_teams": <array>,
  "created_date": <timestamp>,
  "modified_date": <timestamp>,
  "schema_version": <string>
}
```

## profile
```json
profile
{
  "_id": <objectId>,
  "about": <string>,
  "position": <int>,
  "education": <array>,
  "work_experience": <array>,
  "skill": <array>,
  "review": <array>,
  "created_date": <array>,
  "modified_date": <array>,
  "schema_version": <string>
}

education
{
  "_id": <objectId>,
  "institution_name": <string>,
  "started_date": <string>,
  "ended_date": <string>,
  "is_current": <bool>,
  "is_deleted": <bool>,
  "created_date": <timestamp>,
  "modified_date": <timestamp>,
  "schema_version": <string>
}

work_experience
{
  "_id": <objectId>,
  "corporation_name": <string>,
  "started_date": <string>,
  "ended_date": <string>,
  "is_current": <bool>,
  "is_deleted": <bool>,
  "created_date": <timestamp>,
  "modified_date": <timestamp>,
  "schema_version": <string>
}

skill
{
  "_id": <objectId>,
  "skill_name": <string>,
  "level": <int>,
  "is_deleted": <bool>,
  "created_date": <timestamp>,
  "modified_date": <timestamp>,
  "schema_version": <string>
}
```

## review
```json
review
{
  "_id": <objectId>,
  "profile": <objectId>,
  "reviewer": <string>,
  "question": <string>,
  "rating": <int>,
  "is_anonymous": <bool>,
  "created_date": <timestamp>,
  "schema_version": <string>
}
```

## fcm
```json
fcm
{
  "_id": <objectId>,
  "user": <objectId>,
  "content": <string>,
  "is_checked": <bool>,
  "created_date": <timestamp>,
  "schema_version": <string>
}
```