using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;

namespace ConEventServiceRole
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the interface name "IService1" in both code and config file together.
    [ServiceContract]
    public interface IConEventService
    {

        [OperationContract]
        ImageEvent GetImageEventFromId(int imageEventId);

        [OperationContract]
        ImageEvent[] GetAllImageEventFromDate(DateTime fromDate, bool thumbNail);
        
        [OperationContract]
        void SaveImageEvent(ImageEvent imageEvent);  
    }


    // Use a data contract as illustrated in the sample below to add composite types to service operations.
    [DataContract]
    public class ImageEvent
    {
        int imageEventId;
        byte[] imageArray;
        string imageCaption ="";
        List<Person> persons = new List<Person>();
        DateTime eventDate;
        double latitude;
        double longitude;

        [DataMember]
        public int ImageEventId
        {
            get { return imageEventId; }
            set { imageEventId = value; }
        }

        [DataMember]
        public byte[] ImageArray
        {
            get { return imageArray; }
            set { imageArray = value; }
        }

        [DataMember]
        public string ImageCaption
        {
            get { return imageCaption; }
            set { imageCaption = value; }
        }

        [DataMember]
        public double Latitude
        {
            get { return latitude; }
            set { latitude = value; }
        }

        [DataMember]
        public DateTime EventDate
        {
            get { return eventDate; }
            set { eventDate = value; }
        }

        [DataMember]
        public double Longitude
        {
            get { return longitude; }
            set { longitude = value; }
        }

        [DataMember]
        public List<Person> Persons
        {
            get { return persons; }
            set { persons = value; }
        }
    }


    public class Person
    {
        string email = "";
        string firstName = "";
        string lastName = "";
        
        [DataMember]
        public string Email
        {
            get { return email; }
            set { email = value; }
        }

        [DataMember]
        public string LastName
        {
            get { return lastName; }
            set { lastName = value; }
        }
        [DataMember]
        public string FirstName
        {
            get { return firstName; }
            set { firstName = value; }
        }
    }




}
